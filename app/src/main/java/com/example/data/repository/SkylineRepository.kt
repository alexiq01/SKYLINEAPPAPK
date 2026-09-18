package com.example.data.repository

import android.content.Context
import com.example.crypto.OpenSslCrypto
import com.example.data.api.ApiClient
import com.example.data.api.EncryptedPayloadRequest
import com.example.data.api.GenericActionResponse
import com.example.data.api.LoginCredentials
import com.example.data.api.RedeemRequest
import com.example.data.api.SkylineApiService
import com.example.data.api.UnifiedSubscription
import com.example.data.storage.SessionManager
import com.example.worker.NotificationScheduler
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.UUID

class SkylineRepository(
    private val context: Context,
    private val apiService: SkylineApiService = ApiClient.apiService,
    val sessionManager: SessionManager = SessionManager(context)
) {

    suspend fun login(username: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val credentials = LoginCredentials(
                username = username.trim(),
                password = password,
                language = "ar",
                captchaText = null,
                sessionId = UUID.randomUUID().toString()
            )
            val json = ApiClient.gson.toJson(credentials)
            val encryptedPayload = OpenSslCrypto.encrypt(json)
            val request = EncryptedPayloadRequest(payload = encryptedPayload)

            val response = apiService.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                if (!token.isNullOrBlank() && (body.status == 200 || body.status == 1 || body.status == null)) {
                    sessionManager.saveSession(token = token, username = username.trim(), password = password)
                    Result.success(token)
                } else {
                    val msg = body?.message ?: "err_invalid_login"
                    Result.failure(Exception(msg))
                }
            } else {
                val errBody = try { response.errorBody()?.string() } catch (_: Exception) { null }
                val parsedMsg = try {
                    if (!errBody.isNullOrBlank()) {
                        ApiClient.gson.fromJson(errBody, com.example.data.api.LoginResponse::class.java)?.message
                    } else null
                } catch (_: Exception) { null }

                if (response.code() == 401 || response.code() == 403 || parsedMsg != null) {
                    Result.failure(Exception(parsedMsg ?: "err_invalid_login"))
                } else {
                    Result.failure(Exception("server_error_${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sends a background request to the ISP provider gateway at admin.skylineiq.com/user.
     * When connected to the provider network (Skyline Wi-Fi), it returns a successful response
     * and automatically logs the user into their dashboard.
     */
    suspend fun checkProviderConnectionAndAutoLogin(): Result<String> = withContext(Dispatchers.IO) {
        // 1. If user already has stored credentials, attempt direct login with saved credentials first
        val savedUser = sessionManager.getUsername()
        val savedPass = sessionManager.getPassword()
        if (!savedUser.isNullOrBlank() && !savedPass.isNullOrBlank()) {
            val savedLoginRes = login(savedUser, savedPass)
            if (savedLoginRes.isSuccess) {
                return@withContext savedLoginRes
            }
        }

        // 2. Perform background request to provider gateway: http://admin.skylineiq.com/user
        try {
            val request = Request.Builder()
                .url("http://admin.skylineiq.com/user")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; SkylineApp)")
                .header("Accept", "application/json, text/html, */*")
                .get()
                .build()

            val response = ApiClient.okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string().orEmpty()

                // Check if response is JSON with token or user data
                if (responseBodyStr.trim().startsWith("{")) {
                    try {
                        val json = ApiClient.gson.fromJson(responseBodyStr, JsonObject::class.java)
                        val token = if (json.has("token")) json.get("token").asString else null
                        val status = if (json.has("status")) json.get("status").asInt else 200
                        val username = if (json.has("username")) json.get("username").asString
                                      else if (json.has("user")) json.get("user").asString
                                      else "مشترك سكايلان"

                        if (!token.isNullOrBlank() && (status == 200 || status == 1)) {
                            sessionManager.saveSession(token = token, username = username)
                            return@withContext Result.success(token)
                        }
                    } catch (_: Exception) {}
                }

                // Check API user endpoint for active hotspot/provider session
                val apiRequest = Request.Builder()
                    .url("http://admin.skylineiq.com/user/api/index.php/api/user")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; SkylineApp)")
                    .header("Accept", "application/json")
                    .get()
                    .build()

                val apiResponse = ApiClient.okHttpClient.newCall(apiRequest).execute()
                if (apiResponse.isSuccessful) {
                    val apiBodyStr = apiResponse.body?.string().orEmpty()
                    if (apiBodyStr.contains("\"data\"") || apiBodyStr.contains("\"username\"") || apiBodyStr.contains("\"id\"")) {
                        val sessionToken = "provider_session_${UUID.randomUUID()}"
                        sessionManager.saveSession(token = sessionToken, username = "مشترك الشبكة")
                        return@withContext Result.success(sessionToken)
                    }
                }

                // If provider gateway returns authenticated subscriber page without barrier
                if (responseBodyStr.contains("dashboard", ignoreCase = true) || 
                    responseBodyStr.contains("لوحة المشترك") || 
                    responseBodyStr.contains("معلومات الحساب")
                ) {
                    val sessionToken = "provider_session_${UUID.randomUUID()}"
                    sessionManager.saveSession(token = sessionToken, username = "مشترك سكايلان")
                    return@withContext Result.success(sessionToken)
                }

                Result.failure(Exception("not_connected_to_provider"))
            } else {
                Result.failure(Exception("provider_status_${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSubscriptionData(): Result<UnifiedSubscription> = withContext(Dispatchers.IO) {
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) {
            return@withContext Result.failure(Exception("unauthorized"))
        }

        val authHeader = "Bearer $token"

        try {
            val userDeferred = async { apiService.getUser(authHeader) }
            val serviceDeferred = async { apiService.getService(authHeader) }
            val dashboardDeferred = async { apiService.getDashboard(authHeader) }

            val userRes = userDeferred.await()
            val serviceRes = serviceDeferred.await()
            val dashboardRes = dashboardDeferred.await()

            if (userRes.code() == 401 || serviceRes.code() == 401 || dashboardRes.code() == 401) {
                sessionManager.clearSession()
                return@withContext Result.failure(Exception("token_expired"))
            }

            val user = if (userRes.isSuccessful) userRes.body()?.data else null
            val service = if (serviceRes.isSuccessful) serviceRes.body()?.data else null
            val dashboard = if (dashboardRes.isSuccessful) dashboardRes.body()?.data else null

            val unified = UnifiedSubscription(
                user = user,
                service = service,
                dashboard = dashboard,
                lastUpdatedMillis = System.currentTimeMillis(),
                isFromCache = false
            )

            // Cache locally for offline resilience
            sessionManager.saveCache(unified)

            // Schedule WorkManager notifications for subscription expiration
            service?.expiration?.let { exp ->
                NotificationScheduler.scheduleSubscriptionAlerts(context, exp)
                NotificationScheduler.schedulePeriodicSubscriptionCheck(context)
            }

            Result.success(unified)
        } catch (e: Exception) {
            // Return cached data if available on network failure
            val cached = sessionManager.getCachedSubscription()
            if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun redeemCode(pin: String): Result<GenericActionResponse> = withContext(Dispatchers.IO) {
        val token = sessionManager.getToken() ?: return@withContext Result.failure(Exception("unauthorized"))
        try {
            val response = apiService.redeem("Bearer $token", RedeemRequest(pin = pin.trim()))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.status == 200 || body.status == 1 || body.status == null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.message ?: "err_redeem_failed"))
                }
            } else {
                val errBody = try { response.errorBody()?.string() } catch (_: Exception) { null }
                val parsedMsg = try {
                    if (!errBody.isNullOrBlank()) {
                        ApiClient.gson.fromJson(errBody, GenericActionResponse::class.java)?.message
                    } else null
                } catch (_: Exception) { null }
                Result.failure(Exception(parsedMsg ?: "err_redeem_failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}

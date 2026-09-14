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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
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
                    sessionManager.saveSession(token = token, username = username.trim())
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

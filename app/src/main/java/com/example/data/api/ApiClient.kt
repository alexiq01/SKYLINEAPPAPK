package com.example.data.api

import com.example.crypto.OpenSslCrypto
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

object ApiClient {

    const val BASE_URL = "http://admin.skylineiq.com/user/api/index.php/api/"

    private val safeDoubleDeserializer = JsonDeserializer { json: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
        if (json == null || json.isJsonNull) return@JsonDeserializer null
        val str = try { json.asString.trim() } catch (_: Exception) { "" }
        if (str.isEmpty()) null else str.toDoubleOrNull()
    }

    private val safeLongDeserializer = JsonDeserializer { json: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
        if (json == null || json.isJsonNull) return@JsonDeserializer null
        val str = try { json.asString.trim() } catch (_: Exception) { "" }
        if (str.isEmpty()) null else str.toLongOrNull()
    }

    private val safeIntDeserializer = JsonDeserializer { json: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
        if (json == null || json.isJsonNull) return@JsonDeserializer null
        val str = try { json.asString.trim() } catch (_: Exception) { "" }
        if (str.isEmpty()) null else str.toIntOrNull()
    }

    private val safeStringDeserializer = JsonDeserializer { json: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
        if (json == null || json.isJsonNull) return@JsonDeserializer null
        if (json.isJsonPrimitive) {
            json.asString
        } else if (json.isJsonObject) {
            val obj = json.asJsonObject
            val dateElem = obj.get("date") ?: obj.get("formatted") ?: obj.get("value") ?: obj.get("name") ?: obj.get("datetime")
            if (dateElem != null && dateElem.isJsonPrimitive) {
                val str = dateElem.asString
                if (str.contains(".") && str.length > 19) str.substringBefore(".") else str
            } else if (obj.entrySet().isEmpty()) {
                null
            } else {
                obj.toString()
            }
        } else if (json.isJsonArray) {
            if (json.asJsonArray.size() == 0) null else json.asJsonArray.toString()
        } else {
            json.toString()
        }
    }

    val gson: Gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(String::class.java, safeStringDeserializer)
        .registerTypeAdapter(Double::class.javaObjectType, safeDoubleDeserializer)
        .registerTypeAdapter(Double::class.javaPrimitiveType, safeDoubleDeserializer)
        .registerTypeAdapter(Long::class.javaObjectType, safeLongDeserializer)
        .registerTypeAdapter(Long::class.javaPrimitiveType, safeLongDeserializer)
        .registerTypeAdapter(Int::class.javaObjectType, safeIntDeserializer)
        .registerTypeAdapter(Int::class.javaPrimitiveType, safeIntDeserializer)
        .create()

    /**
     * Interceptor to automatically decrypt response bodies if encrypted with OpenSSL AES salted payload.
     * Handles raw Base64, JSON with "payload" field, and quoted Base64 strings.
     * Leaves standard JSON completely intact.
     */
    private val responseDecryptInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response: Response = chain.proceed(request)
        val body = response.body ?: return@Interceptor response

        val rawString = body.string()
        val mediaType = body.contentType() ?: "application/json; charset=utf-8".toMediaTypeOrNull()

        val decryptedString: String = try {
            val trimmed = rawString.trim()
            val clean = if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length > 2) {
                trimmed.substring(1, trimmed.length - 1).trim()
            } else {
                trimmed
            }

            if (clean.startsWith("U2FsdGVkX1")) {
                OpenSslCrypto.decrypt(clean)
            } else if (trimmed.startsWith("{") && trimmed.contains("\"payload\"")) {
                val jsonObject = gson.fromJson(trimmed, JsonObject::class.java)
                if (jsonObject.has("payload")) {
                    val encryptedPayload = jsonObject.get("payload").asString
                    OpenSslCrypto.decrypt(encryptedPayload)
                } else {
                    rawString
                }
            } else {
                rawString
            }
        } catch (_: Exception) {
            rawString
        }

        val newBody = decryptedString.toResponseBody(mediaType)
        response.newBuilder()
            .body(newBody)
            .build()
    }

    /**
     * Safe logging interceptor that NEVER prints passwords or sensitive tokens.
     */
    private val safeLoggingInterceptor = HttpLoggingInterceptor { message ->
        if (message.contains("authorization", ignoreCase = true) ||
            message.contains("password", ignoreCase = true) ||
            message.contains("bearer", ignoreCase = true)
        ) {
            // Suppress sensitive credentials from logcat
            return@HttpLoggingInterceptor
        }
        // Minimal non-sensitive logging if needed
    }.apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(responseDecryptInterceptor)
        .addInterceptor(safeLoggingInterceptor)
        .build()

    val apiService: SkylineApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(SkylineApiService::class.java)
}

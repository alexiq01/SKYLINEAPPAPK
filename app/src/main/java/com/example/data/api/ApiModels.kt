package com.example.data.api

import com.google.gson.annotations.SerializedName

data class EncryptedPayloadRequest(
    @SerializedName("payload")
    val payload: String
)

data class LoginCredentials(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("language")
    val language: String = "ar",
    @SerializedName("captcha_text")
    val captchaText: String? = null,
    @SerializedName("session_id")
    val sessionId: String? = null
)

data class LoginResponse(
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("message")
    val message: String? = null
)

data class ApiResponse<T>(
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("message")
    val message: String? = null
)

fun extractStringFromAny(raw: Any?): String? {
    if (raw == null) return null
    return when (raw) {
        is String -> {
            val trimmed = raw.trim()
            if (trimmed.isEmpty() || trimmed.equals("null", ignoreCase = true)) null else trimmed
        }
        is Number -> raw.toString()
        is Boolean -> raw.toString()
        is Map<*, *> -> {
            val dateVal = raw["date"] ?: raw["formatted"] ?: raw["value"] ?: raw["name"] ?: raw["datetime"] ?: raw["string"]
            if (dateVal != null) {
                val str = dateVal.toString().trim()
                if (str.contains(".") && str.length > 19) {
                    str.substringBefore(".")
                } else {
                    str
                }
            } else if (raw.isEmpty()) {
                null
            } else {
                raw.toString()
            }
        }
        else -> raw.toString()
    }
}

data class UserAccountData(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("balance")
    val balance: Double? = null,
    @SerializedName("loan_balance")
    val loanBalance: Double? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("company")
    val company: String? = null,
    @SerializedName("contract_id")
    val contractIdRaw: Any? = null,
    @SerializedName("registered_on")
    val registeredOnRaw: Any? = null,
    @SerializedName("static_ip")
    val staticIpRaw: Any? = null,
    @SerializedName("auto_renew")
    val autoRenew: Int? = null,
    @SerializedName("auto_renew_string")
    val autoRenewString: String? = null
) {
    val contractId: String? get() = extractStringFromAny(contractIdRaw)
    val registeredOn: String? get() = extractStringFromAny(registeredOnRaw)
    val staticIp: String? get() = extractStringFromAny(staticIpRaw)
}

data class ServiceLimits(
    @SerializedName("rxtx_bytes")
    val rxTxBytes: Long? = null
)

data class SubscriptionStatus(
    @SerializedName("expiration")
    val expiration: Any? = null
) {
    val isActive: Boolean
        get() = when (expiration) {
            is Boolean -> expiration
            is Number -> expiration.toInt() == 1
            is String -> expiration.equals("active", ignoreCase = true) || expiration.equals("1", ignoreCase = true) || expiration.equals("true", ignoreCase = true)
            else -> false
        }
}

data class ServiceData(
    @SerializedName("profile_name")
    val profileName: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("status")
    val status: Any? = null,
    @SerializedName("subscription_status")
    val subscriptionStatus: SubscriptionStatus? = null,
    @SerializedName("expiration")
    val expirationRaw: Any? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("profile_id")
    val profileId: Long? = null,
    @SerializedName("download_limit")
    val downloadLimitRaw: Any? = null,
    @SerializedName("upload_limit")
    val uploadLimitRaw: Any? = null,
    @SerializedName("traffic_limit")
    val trafficLimitRaw: Any? = null,
    @SerializedName("limits")
    val limits: ServiceLimits? = null
) {
    val expiration: String? get() = extractStringFromAny(expirationRaw)
    val downloadLimit: String? get() = extractStringFromAny(downloadLimitRaw)
    val uploadLimit: String? get() = extractStringFromAny(uploadLimitRaw)
    val trafficLimit: String? get() = extractStringFromAny(trafficLimitRaw)

    val isOnline: Boolean
        get() = when (status) {
            is Boolean -> status
            is Number -> status.toInt() == 1
            is String -> status.equals("online", ignoreCase = true) || status.equals("1", ignoreCase = true) || status.equals("true", ignoreCase = true)
            else -> false
        }
}

data class DashboardData(
    @SerializedName("remaining_days")
    val remainingDaysRaw: Any? = null,
    @SerializedName("balance")
    val balance: Double? = null,
    @SerializedName("unpaid_invoices")
    val unpaidInvoices: Double? = null,
    @SerializedName("remaining_traffic")
    val remainingTraffic: Any? = null,
    @SerializedName("remaining_uptime")
    val remainingUptime: Any? = null,
    @SerializedName("loan")
    val loan: Any? = null
) {
    val remainingDays: Int
        get() = when (remainingDaysRaw) {
            is Number -> remainingDaysRaw.toInt()
            is String -> remainingDaysRaw.toIntOrNull() ?: 0
            else -> 0
        }
}

data class PackageItem(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("description")
    val description: String? = null
)

data class RedeemRequest(
    @SerializedName("pin")
    val pin: String
)

data class ActivateRequest(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("current_password")
    val currentPassword: String?
)

data class ExtendRequest(
    @SerializedName("profile_id")
    val profileId: Long?,
    @SerializedName("current_password")
    val currentPassword: String?
)

data class ChangeServiceRequest(
    @SerializedName("new_service")
    val newService: Long?,
    @SerializedName("current_password")
    val currentPassword: String?
)

data class GenericActionResponse(
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("message")
    val message: String? = null
)

/**
 * Unified cached state representing complete user subscription view
 */
data class UnifiedSubscription(
    val user: UserAccountData? = null,
    val service: ServiceData? = null,
    val dashboard: DashboardData? = null,
    val lastUpdatedMillis: Long = 0L,
    val isFromCache: Boolean = false
)

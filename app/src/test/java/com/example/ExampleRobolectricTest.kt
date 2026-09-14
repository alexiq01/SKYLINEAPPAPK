package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.crypto.OpenSslCrypto
import com.example.data.api.ApiClient
import com.example.data.api.UserAccountData
import com.example.data.storage.SessionManager
import com.example.worker.NotificationScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("اشتراكاتي", appName)
    }

    @Test
    fun `test OpenSSL crypto encryption and decryption roundtrip`() {
        val testPayload = """{"username":"test_user","password":"secret_password","language":"ar"}"""
        val encrypted = OpenSslCrypto.encrypt(testPayload)

        assertNotNull(encrypted)
        assertTrue(encrypted.isNotEmpty())

        val decrypted = OpenSslCrypto.decrypt(encrypted)
        assertEquals(testPayload, decrypted)
    }

    @Test
    fun `test date parser and remaining days calculation`() {
        val dateIso = "2026-12-31 23:59:59"
        val millis = NotificationScheduler.parseDateToMillis(dateIso)
        assertNotNull(millis)
        assertTrue(millis!! > 0)

        // Slashed format
        val dateSlash = "2026/12/31 12:00:00"
        val millisSlash = NotificationScheduler.parseDateToMillis(dateSlash)
        assertNotNull(millisSlash)

        // Date in future should be active
        val futureDate = "2030-01-01 00:00:00"
        assertTrue(NotificationScheduler.isSubscriptionActive(futureDate))
        assertTrue(NotificationScheduler.calculateRemainingDays(futureDate) > 100)

        // Date in past should not be active
        val pastDate = "2020-01-01 00:00:00"
        assertEquals(false, NotificationScheduler.isSubscriptionActive(pastDate))
        assertEquals(0, NotificationScheduler.calculateRemainingDays(pastDate))

        // Format expiration with remaining time in Arabic
        val formattedArabicDate = NotificationScheduler.formatArabicDate("2026-10-13 14:00:00")
        assertNotNull(formattedArabicDate)
        assertTrue(formattedArabicDate.contains("أكتوبر") || formattedArabicDate.contains("13"))

        val formattedWithRemaining = NotificationScheduler.formatExpirationWithRemainingTime("2026-10-13 14:00:00")
        assertNotNull(formattedWithRemaining)
        assertTrue(formattedWithRemaining.startsWith("ينتهي في"))
        assertTrue(formattedWithRemaining.contains("متبقي"))
    }

    @Test
    fun `test thread safe date parsing under concurrent access`() {
        val executor = Executors.newFixedThreadPool(8)
        val latch = CountDownLatch(50)
        var hasFailure = false

        for (i in 0 until 50) {
            executor.submit {
                try {
                    val m = NotificationScheduler.parseDateToMillis("2026-10-15 14:30:00")
                    if (m == null || m <= 0) {
                        hasFailure = true
                    }
                } catch (_: Exception) {
                    hasFailure = true
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await(5, TimeUnit.SECONDS)
        executor.shutdown()
        assertEquals(false, hasFailure)
    }

    @Test
    fun `test safe Gson deserializer handles empty strings for numbers without crashing`() {
        val jsonWithEmptyNumbers = """
            {
                "username": "user123",
                "balance": "",
                "loan_balance": ""
            }
        """.trimIndent()

        val parsed = ApiClient.gson.fromJson(jsonWithEmptyNumbers, UserAccountData::class.java)
        assertNotNull(parsed)
        assertEquals("user123", parsed.username)
        assertNull(parsed.balance)
        assertNull(parsed.loanBalance)
    }

    @Test
    fun `test registered_on as object deserializes properly without crash`() {
        // PHP DateTime object representation:
        val jsonWithObjectDate = """
            {
                "status": 200,
                "data": {
                    "id": 1052,
                    "name": "Ali Hassan",
                    "username": "ali_hassan",
                    "balance": 15000.0,
                    "contract_id": "CONT-9921",
                    "registered_on": {
                        "date": "2024-03-15 11:20:45.000000",
                        "timezone_type": 3,
                        "timezone": "Asia/Baghdad"
                    },
                    "static_ip": null,
                    "auto_renew": 1
                }
            }
        """.trimIndent()

        val type = object : com.google.gson.reflect.TypeToken<com.example.data.api.ApiResponse<UserAccountData>>() {}.type
        val response: com.example.data.api.ApiResponse<UserAccountData> = ApiClient.gson.fromJson(jsonWithObjectDate, type)
        assertNotNull(response.data)
        assertEquals("Ali Hassan", response.data?.name)
        assertEquals("2024-03-15 11:20:45", response.data?.registeredOn)

        // Empty object representation {}
        val jsonWithEmptyObjectDate = """
            {
                "id": 1052,
                "username": "ali_hassan",
                "registered_on": {}
            }
        """.trimIndent()
        val parsedEmpty = ApiClient.gson.fromJson(jsonWithEmptyObjectDate, UserAccountData::class.java)
        assertNotNull(parsedEmpty)
        assertNull(parsedEmpty.registeredOn)

        // String representation
        val jsonWithStringDate = """
            {
                "id": 1052,
                "username": "ali_hassan",
                "registered_on": "2024-03-15"
            }
        """.trimIndent()
        val parsedString = ApiClient.gson.fromJson(jsonWithStringDate, UserAccountData::class.java)
        assertNotNull(parsedString)
        assertEquals("2024-03-15", parsedString.registeredOn)
    }

    @Test
    fun `test session manager save and clear`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val session = SessionManager(context)

        session.saveSession(token = "test_token_xyz", username = "skyline_user")
        assertTrue(session.hasValidSession())
        assertEquals("test_token_xyz", session.getToken())
        assertEquals("skyline_user", session.getUsername())

        session.clearSession()
        assertEquals(false, session.hasValidSession())
        assertNull(session.getToken())
    }

    @Test
    fun `test developer contact constants`() {
        assertEquals("https://www.instagram.com/y_as.01", com.example.ui.components.DEVELOPER_INSTAGRAM_URL)
        assertEquals("+9647830528876", com.example.ui.components.DEVELOPER_WHATSAPP_NUMBER)
        assertEquals("4659615480", com.example.ui.components.DEVELOPER_SUPER_QI_NUMBER)
    }
}

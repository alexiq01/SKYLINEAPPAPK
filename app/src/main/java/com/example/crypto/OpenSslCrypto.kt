package com.example.crypto

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object OpenSslCrypto {

    const val DEFAULT_KEY = "abcdefghijuklmno0123456789012345"
    private val SALTED_HEADER = "Salted__".toByteArray(StandardCharsets.US_ASCII)

    /**
     * Encrypts plaintext string using AES-256-CBC compatible with OpenSSL and CryptoJS.
     * Returns Base64("Salted__" + 8_bytes_salt + ciphertext)
     */
    fun encrypt(plainText: String, passphrase: String = DEFAULT_KEY): String {
        val salt = ByteArray(8)
        SecureRandom().nextBytes(salt)

        val (key, iv) = deriveKeyAndIv(passphrase.toByteArray(StandardCharsets.UTF_8), salt)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        val cipherText = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        val combined = ByteArray(SALTED_HEADER.size + salt.size + cipherText.size)
        System.arraycopy(SALTED_HEADER, 0, combined, 0, SALTED_HEADER.size)
        System.arraycopy(salt, 0, combined, SALTED_HEADER.size, salt.size)
        System.arraycopy(cipherText, 0, combined, SALTED_HEADER.size + salt.size, cipherText.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts OpenSSL/CryptoJS AES-256-CBC salted base64 string.
     * If the string is not salted base64 or plaintext JSON, returns the original text.
     */
    fun decrypt(cipherBase64: String, passphrase: String = DEFAULT_KEY): String {
        return try {
            val decoded = Base64.decode(cipherBase64.trim(), Base64.DEFAULT)
            if (decoded.size < 16) return cipherBase64

            val isSalted = decoded.take(8).toByteArray().contentEquals(SALTED_HEADER)
            if (!isSalted) return cipherBase64

            val salt = ByteArray(8)
            System.arraycopy(decoded, 8, salt, 0, 8)

            val cipherText = ByteArray(decoded.size - 16)
            System.arraycopy(decoded, 16, cipherText, 0, cipherText.size)

            val (key, iv) = deriveKeyAndIv(passphrase.toByteArray(StandardCharsets.UTF_8), salt)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
            val decryptedBytes = cipher.doFinal(cipherText)

            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (_: Exception) {
            cipherBase64
        }
    }

    /**
     * OpenSSL EVP_BytesToKey with MD5:
     * Derives 32 bytes AES key + 16 bytes IV (total 48 bytes) using repeated MD5.
     */
    private fun deriveKeyAndIv(passphrase: ByteArray, salt: ByteArray): Pair<ByteArray, ByteArray> {
        val md5 = MessageDigest.getInstance("MD5")
        val derived = ByteArray(48)
        var offset = 0
        var prevDigest = ByteArray(0)

        while (offset < 48) {
            md5.reset()
            if (prevDigest.isNotEmpty()) {
                md5.update(prevDigest)
            }
            md5.update(passphrase)
            md5.update(salt)
            val currentDigest = md5.digest()
            val toCopy = minOf(currentDigest.size, 48 - offset)
            System.arraycopy(currentDigest, 0, derived, offset, toCopy)
            offset += toCopy
            prevDigest = currentDigest
        }

        val key = ByteArray(32)
        val iv = ByteArray(16)
        System.arraycopy(derived, 0, key, 0, 32)
        System.arraycopy(derived, 32, iv, 0, 16)

        return Pair(key, iv)
    }
}

package com.example.nq.recyclerViewTcikets

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

class EncryptionUtility {

    private val passphrase = "Road_to_15k"
    private val salt = "RandomSalt"
    private val iterations = 65536
    private val keyLength = 256

    private fun generateSecretKey(): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(passphrase.toCharArray(), salt.toByteArray(), iterations, keyLength)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, "AES")
    }

    internal fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        // val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val secretKey = generateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedData)
    }

    internal fun decrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        // val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val secretKey = generateSecretKey()
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedData = cipher.doFinal(Base64.getDecoder().decode(data))
        return String(decryptedData)
    }
}
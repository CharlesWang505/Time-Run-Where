package com.timerunwhere.data.local

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureTokenStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("secure_pairing", Context.MODE_PRIVATE)

    private val _tokenFlow = MutableStateFlow(readToken().orEmpty())
    val tokenFlow: StateFlow<String> = _tokenFlow

    fun readToken(): String? {
        val encrypted = prefs.getString(KEY_TOKEN, null) ?: return null
        return runCatching {
            val allBytes = Base64.decode(encrypted, Base64.NO_WRAP)
            val iv = allBytes.copyOfRange(0, IV_SIZE)
            val cipherText = allBytes.copyOfRange(IV_SIZE, allBytes.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(TAG_SIZE_BITS, iv))
            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        }.getOrNull()
    }

    fun saveToken(rawToken: String) {
        val token = rawToken.filter(Char::isDigit).take(6)
        if (token.length != 6) return
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val cipherText = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val payload = cipher.iv + cipherText
        prefs.edit().putString(KEY_TOKEN, Base64.encodeToString(payload, Base64.NO_WRAP)).apply()
        _tokenFlow.value = token
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let {
            return it.secretKey
        }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    companion object {
        private const val KEY_ALIAS = "time_run_where_pairing_token"
        private const val KEY_TOKEN = "encrypted_token"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val TAG_SIZE_BITS = 128
    }
}

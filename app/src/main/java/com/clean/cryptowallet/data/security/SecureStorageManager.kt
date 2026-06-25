package com.clean.cryptowallet.data.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * यह क्लास यूजर की संवेदनशील जानकारी जैसे 24-वर्ड फ्रेज़, पब्लिक एड्रेस, माइनिंग कॉइन्स,
 * और यूजर प्रोफाइल को मिलिट्री-ग्रेड एन्क्रिप्शन के साथ डिवाइस के अंदर स्टोर करती है।
 */
@RequiresApi(Build.VERSION_CODES.M)
class SecureStorageManager(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        // 1. एंड्रॉइड की-स्टोर (Android Keystore) का उपयोग करके एक मास्टर की (Master Key) जनरेट करना
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // 2. एन्क्रिप्टेड शेयर्ड प्रेफरेंसेस को इनिशियलाइज करना
        // यह फाइलों को डिवाइस में लिखते समय खुद-ब-खुद AES-256 से एन्क्रिप्ट कर देता है
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            SECURE_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * यूजर के 24 शब्दों के गुप्त फ्रेज़ को सुरक्षित रूप से सेव करना।
     */
    fun saveMnemonic(mnemonic: List<String>): Boolean {
        val convertedString = mnemonic.joinToString(",")
        return sharedPreferences.edit().putString(KEY_MNEMONIC, convertedString).commit()
    }

    /**
     * सुरक्षित तरीके से सेव किए गए 24 शब्दों को वापस पाना।
     */
    fun getMnemonic(): List<String> {
        val savedData = sharedPreferences.getString(KEY_MNEMONIC, null)
        return if (!savedData.isNullOrEmpty()) {
            savedData.split(",")
        } else {
            emptyList()
        }
    }

    /**
     * यूजर का वॉलेट एड्रेस सुरक्षित स्टोर करना।
     */
    fun saveWalletAddress(address: String): Boolean {
        return sharedPreferences.edit().putString(KEY_WALLET_ADDRESS, address).commit()
    }

    /**
     * वॉलेट एड्रेस वापस पाना।
     */
    fun getWalletAddress(): String {
        return sharedPreferences.getString(KEY_WALLET_ADDRESS, "") ?: ""
    }

    /**
     * हमारे कॉइन की माइनिंग के स्टेप्स और कुल बैलेंस को सुरक्षित रखना।
     */
    fun saveMiningBalance(balance: Double): Boolean {
        return sharedPreferences.edit().putFloat(KEY_MINING_BALANCE, balance.toFloat()).commit()
    }

    /**
     * माइन किए गए कॉइन्स का बैलेंस देखना।
     */
    fun getMiningBalance(): Double {
        return sharedPreferences.getFloat(KEY_MINING_BALANCE, 0.0f).toDouble()
    }

    /**
     * यूजर की पर्सनल डिटेल्स (जैसे नाम या ईमेल) सुरक्षित सेव करना।
     */
    fun saveUserDetails(username: String, email: String): Boolean {
        return sharedPreferences.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_USER_EMAIL, email)
            .commit()
    }

    /**
     * यूजर का नाम वापस पाना।
     */
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "Anonymous User") ?: "Anonymous User"
    }

    /**
     * पूरा डेटा साफ करना (Reset/Logout के समय उपयोग के लिए)।
     */
    fun clearAllSecureData(): Boolean {
        return sharedPreferences.edit().clear().commit()
    }

    companion object {
        private const val SECURE_FILE_NAME = "secure_sovereign_wallet_prefs"
        private const val KEY_MNEMONIC = "encrypted_mnemonic_phrase"
        private const val KEY_WALLET_ADDRESS = "encrypted_wallet_address"
        private const val KEY_MINING_BALANCE = "secure_mining_balance"
        private const val KEY_USERNAME = "secure_username"
        private const val KEY_USER_EMAIL = "secure_user_email"
    }
}

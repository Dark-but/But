package com.clean.cryptowallet.data.security

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest

/**
 * यूजर के सिक्योरिटी पिन को हैश (SHA-256) करके सुरक्षित वेरीफाई करने वाला इंजन।
 */
@RequiresApi(Build.VERSION_CODES.M)
class AppLockEngine(context: Context) {

    private val secureStorage = SecureStorageManager(context)
    private val sharedPrefs = context.getSharedPreferences("app_lock_meta", Context.MODE_PRIVATE)

    /**
     * पहली बार नया सिक्योरिटी पिन सेट करना (पूरी तरह से हैश करके सेव होगा)
     */
    fun setupNewPin(pin: String): Boolean {
        if (pin.length < 4) return false
        val hashedPin = hashPinWithSha256(pin)
        
        // सुरक्षित तरीके से मेटाडाटा सेव करना कि पिन सेट हो चुका है
        return sharedPrefs.edit()
            .putString("hashed_security_pin", hashedPin)
            .putBoolean("is_pin_configured", true)
            .commit()
    }

    /**
     * चेक करना कि क्या यूजर ने पहले से पिन सेट कर रखा है
     */
    fun isPinConfigured(): Boolean {
        return sharedPrefs.getBoolean("is_pin_configured", false)
    }

    /**
     * यूजर द्वारा डाले गए पिन को मैच/वेरीफाई करना
     */
    fun verifyUserPin(inputPin: String): Boolean {
        val savedHashedPin = sharedPrefs.getString("hashed_security_pin", null) ?: return false
        val inputHashedPin = hashPinWithSha256(inputPin)
        
        return savedHashedPin == inputHashedPin
    }

    /**
     * पिन को सादे टेक्स्ट में सेव करने के बजाय SHA-256 से वन-वे एन्क्रिप्ट करने का लॉजिक
     */
    private fun hashPinWithSha256(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

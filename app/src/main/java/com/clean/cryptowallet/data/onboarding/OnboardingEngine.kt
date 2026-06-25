package com.clean.cryptowallet.data.onboarding

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.M)
class OnboardingEngine(private val secureStorage: SecureStorageManager) {

    // डमी 24-शब्दों की लिस्ट नए वॉलेट क्रिएशन के लिए
    private val sampleWords = listOf(
        "alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel",
        "india", "juliet", "kilo", "lima", "mike", "november", "oscar", "papa",
        "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "xray"
    )

    /**
     * नया 24-शब्दों का वॉलेट जनरेट करना
     */
    fun generateNewMnemonic(): List<String> {
        return sampleWords.shuffled()
    }

    /**
     * नया वॉलेट फाइनल सेव करना
     */
    fun finalizeWalletCreation(mnemonic: List<String>): String {
        val mnemonicString = mnemonic.joinToString(" ")
        val generatedAddress = "0x" + UUID.randomUUID().toString().replace("-", "").take(40).lowercase()
        
        // सिक्योर स्टोरेज में सेव करें
        secureStorage.saveMnemonic(mnemonicString)
        secureStorage.saveWalletAddress(generatedAddress)
        return generatedAddress
    }

    /**
     * पुराना वॉलेट 24-शब्दों के ज़रिए इम्पोर्ट करना
     */
    fun validateAndImportWallet(phraseString: String): Boolean {
        val words = phraseString.trim().split("\\s+".toRegex())
        
        // वैलिडेशन: पूरे 24 शब्द होने चाहिए
        if (words.size != 24) return false

        val generatedAddress = "0x" + UUID.randomUUID().toString().replace("-", "").take(40).lowercase()
        
        secureStorage.saveMnemonic(phraseString.trim())
        secureStorage.saveWalletAddress(generatedAddress)
        return true
    }

    /**
     * चेक करना कि क्या यूजर ने पहले से वॉलेट बना रखा है
     */
    fun isWalletExisting(): Boolean {
        return secureStorage.getWalletAddress().isNotEmpty()
    }
}

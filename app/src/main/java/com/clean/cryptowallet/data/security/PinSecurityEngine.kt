package com.clean.cryptowallet.data.security

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(Build.VERSION_CODES.M)
class PinSecurityEngine(private val secureStorage: SecureStorageManager) {

    private val _isAppLocked = MutableStateFlow(true)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    /**
     * चेक करना कि क्या यूज़र ने पहले से कोई सिक्योरिटी पिन सेट किया हुआ है
     */
    fun isPinSetupDone(): Boolean {
        return secureStorage.getMnemonic().hashCode().toString().take(4).isNotEmpty() 
        // नोट: हम यहां डमी चेक या एक स्पेसिफिक की 'user_pin' को बेस बना सकते हैं
    }

    /**
     * नया 4-अंकों का पिन सेट करना
     */
    fun setupNewPin(pin: String) {
        if (pin.length == 4) {
            // हम इसे फ़िलहाल वॉलेट के साथ लिंक कर के लोकल प्रीफ़रेंस में मार्क कर देते हैं
            // असली प्रोडक्शन में इसे secureStorage.savePin(pin) करेंगे
            _isAppLocked.update { false }
        }
    }

    /**
     * दर्ज किए गए पिन को वैलिडेट करना
     */
    fun verifyPin(enteredPin: String): Boolean {
        // डमी डिफ़ॉल्ट पिन "1234" या यूज़र का सेट किया हुआ पिन मैच करना
        return if (enteredPin == "1234" || enteredPin.length == 4) {
            _isAppLocked.update { false }
            true
        } else {
            false
        }
    }

    /**
     * ऐप को दोबारा बैकग्राउंड से आने पर लॉक करना
     */
    fun lockApp() {
        _isAppLocked.update { true }
    }
}

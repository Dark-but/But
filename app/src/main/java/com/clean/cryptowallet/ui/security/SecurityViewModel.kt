package com.clean.cryptowallet.ui.security

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.clean.cryptowallet.data.security.AppLockEngine
import com.clean.cryptowallet.data.security.AppLockState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(Build.VERSION_CODES.M)
class SecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val lockEngine = AppLockEngine(application.applicationContext)

    private val _lockState = MutableStateFlow(AppLockState())
    val lockState: StateFlow<AppLockState> = _lockState.asStateFlow()

    init {
        // चेक करना कि ऐप में पिन पहले से सेट है या नया सेटअप करना है
        val isConfigured = lockEngine.isPinConfigured()
        _lockState.update { it.copy(isPinSetupComplete = isConfigured) }
    }

    /**
     * कीपैड से नंबर दबाने पर पिन इनपुट कलेक्ट करना
     */
    fun onNumberPressed(number: String) {
        if (_lockState.value.currentInputPin.length >= 4) return

        _lockState.update { currentState ->
            val updatedPin = currentState.currentInputPin + number
            
            // जैसे ही 4 डिजिट पूरे हों, ऑटोमैटिक चेक करना
            if (updatedPin.length == 4) {
                handlePinVerification(updatedPin, currentState)
            } else {
                currentState.copy(currentInputPin = updatedPin, wrongPinEntered = false)
            }
        }
    }

    /**
     * आखिरी नंबर को मिटाना (Backspace)
     */
    fun onBackspacePressed() {
        _lockState.update { currentState ->
            if (currentState.currentInputPin.isNotEmpty()) {
                currentState.copy(currentInputPin = currentState.currentInputPin.dropLast(1), wrongPinEntered = false)
            } else {
                currentState
            }
        }
    }

    private fun handlePinVerification(pin: String, currentState: AppLockState) {
        if (!currentState.isPinSetupComplete) {
            // स्थिति A: नया पिन सेट कर रहे हैं
            val success = lockEngine.setupNewPin(pin)
            if (success) {
                _lockState.update { it.copy(isPinSetupComplete = true, currentInputPin = "", isAppLacked = false) }
            }
        } else {
            // स्थिति B: पुराना पिन वेरीफाई कर रहे हैं
            val isCorrect = lockEngine.verifyUserPin(pin)
            if (isCorrect) {
                _lockState.update { it.copy(isAppLacked = false, currentInputPin = "", wrongPinEntered = false) }
            } else {
                _lockState.update { 
                    it.copy(
                        currentInputPin = "", 
                        wrongPinEntered = true, 
                        remainingAttempts = currentState.remainingAttempts - 1
                    ) 
                }
            }
        }
    }
}

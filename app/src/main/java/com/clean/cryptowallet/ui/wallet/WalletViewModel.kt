package com.clean.cryptowallet.ui.wallet

import androidx.lifecycle.ViewModel
import com.clean.cryptowallet.data.wallet.WalletEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// स्क्रीन पर डेटा किस रूप में दिखेगा, उसकी स्टेट (UI State)
data class WalletUiState(
    val mnemonicWords: List<String> = emptyList(),
    val walletAddress: String = "",
    val isWalletCreated: Boolean = false
)

class WalletViewModel : ViewModel() {

    // वॉलेट इंजन का ऑब्जेक्ट जो हमने पिछले स्टेप में बनाया था
    private val walletEngine = WalletEngine()

    // यह स्टेट ऐप के अंदर का डेटा संभालेगी (प्राइवेट रहेगी)
    private val _uiState = MutableStateFlow(WalletUiState())
    
    // यह स्टेट सिर्फ स्क्रीन को डेटा दिखाने (Read-only) के काम आएगी
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    // जब यूज़र बटन दबाएगा, तब यह फंक्शन नया 24 शब्दों का वॉलेट बनाएगा
    fun createNewWallet() {
        val words = walletEngine.generateNewMnemonic()
        val address = walletEngine.deriveWalletAddress(words)

        _uiState.update { currentState ->
            currentState.copy(
                mnemonicWords = words,
                walletAddress = address,
                isWalletCreated = true
            )
        }
    }

    // वॉलेट को रीसेट या साफ़ करने के लिए फंक्शन
    fun clearWallet() {
        _uiState.update {
            WalletUiState()
        }
    }
}

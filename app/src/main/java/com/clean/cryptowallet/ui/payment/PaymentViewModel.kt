package com.clean.cryptowallet.ui.payment

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clean.cryptowallet.data.payment.ButtPayTransaction
import com.clean.cryptowallet.data.payment.PaymentEngine
import com.clean.cryptowallet.data.payment.PaymentUiState
import com.clean.cryptowallet.data.payment.TransactionStatus
import com.clean.cryptowallet.data.security.SecureStorageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorageManager(application.applicationContext)
    private val paymentEngine = PaymentEngine(secureStorage)

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _historyList = mutableListOf<ButtPayTransaction>()

    /**
     * पेमेंट भेजने की प्रक्रिया (Google Pay की तरह लोडिंग एनीमेशन के साथ)
     */
    fun sendCoins(receiverAddress: String, amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        
        if (receiverAddress.isBlank() || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Invalid Address or Amount") }
            return
        }

        _uiState.update { it.copy(isProcessing = true, errorMessage = null, paymentSuccess = false) }

        viewModelScope.launch {
            delay(2000) // Google Pay जैसा 2 सेकंड का सिक्योर प्रोसेसिंग डिले फील देने के लिए

            paymentEngine.processPeerToPeerPayment(receiverAddress, amount) { transaction ->
                if (transaction.status == TransactionStatus.SUCCESS) {
                    _historyList.add(0, transaction) // हिस्ट्री में सबसे ऊपर जोड़ना
                    _uiState.update { 
                        it.copy(
                            isProcessing = false, 
                            paymentSuccess = true,
                            transactionHistory = _historyList.toList()
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isProcessing = false, 
                            paymentSuccess = false, 
                            errorMessage = "Payment Failed: Insufficient Balance!"
                        ) 
                    }
                }
            }
        }
    }

    fun resetPaymentState() {
        _uiState.update { it.copy(paymentSuccess = false, errorMessage = null, isProcessing = false) }
    }
}

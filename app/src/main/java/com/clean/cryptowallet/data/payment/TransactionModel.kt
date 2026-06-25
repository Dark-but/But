package com.clean.cryptowallet.data.payment

/**
 * बट पे (ButtPay) के अंतर्गत होने वाले हर एक ट्रांजैक्शन का पूरा डेटा स्ट्रक्चर।
 */
data class ButtPayTransaction(
    val transactionId: String,
    val senderAddress: String,
    val receiverAddress: String,
    val amount: Double,
    val timestamp: Long,
    val status: TransactionStatus,
    val transactionFee: Double = 0.0001 // हमारे नेटवर्क की मिनिमम फिक्स्ड फीस
)

enum class TransactionStatus {
    PENDING, SUCCESS, FAILED
}

/**
 * यूआई स्क्रीन की लाइव स्टेट को मैनेज करने के लिए।
 */
data class PaymentUiState(
    val isProcessing: Boolean = false,
    val paymentSuccess: Boolean = false,
    val errorMessage: String? = null,
    val transactionHistory: List<ButtPayTransaction> = emptyList()
)

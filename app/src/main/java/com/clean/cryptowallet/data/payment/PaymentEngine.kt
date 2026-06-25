package com.clean.cryptowallet.data.payment

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import com.clean.cryptowallet.data.admin.AdminConfig
import java.security.MessageDigest
import java.util.UUID

/**
 * बट पे (ButtPay) का कोर इंजन जो एडमिन फीस काटकर कॉइन्स को ट्रांसफर करता है।
 */
@RequiresApi(Build.VERSION_CODES.M)
class PaymentEngine(private val secureStorage: SecureStorageManager) {

    /**
     * P2P ट्रांसफर - इसमें से 1% एडमिन फीस कटेगी और बची हुई रकम रिसीवर को जाएगी।
     */
    fun processPeerToPeerPayment(
        receiverAddress: String,
        amount: Double,
        onResult: (ButtPayTransaction) -> Unit
    ) {
        val currentBalance = secureStorage.getMiningBalance()
        val senderAddress = secureStorage.getWalletAddress()

        // 1. एडमिन फीस की गणना (1% फीस)
        val adminFee = amount * 0.01
        val totalDeduction = amount + adminFee

        // 2. वैलिडेशन: क्या यूजर के पास अमाउंट + 1% एड敏 फीस है?
        if (currentBalance < totalDeduction || amount <= 0.0) {
            val failedTx = ButtPayTransaction(
                transactionId = "FAILED_" + UUID.randomUUID().toString().take(8),
                senderAddress = senderAddress,
                receiverAddress = receiverAddress,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.FAILED,
                transactionFee = adminFee
            )
            onResult(failedTx)
            return
        }

        // 3. बैलेंस काटना (अमाउंट और एडमिन फीस दोनों कटेंगे)
        val newBalance = currentBalance - totalDeduction
        secureStorage.saveMiningBalance(newBalance)

        // 4. क्रिप्टोग्राफिक ट्रांजैक्शन आईडी जनरेट करना (SHA-256)
        val rawTxData = "$senderAddress$receiverAddress$amount$adminFee${System.currentTimeMillis()}"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawTxData.toByteArray(Charsets.UTF_8))
        val txId = "tx_" + hashBytes.joinToString("") { "%02x".format(it) }.take(16)

        // 5. सक्सेस ट्रांजैक्शन
        val successTx = ButtPayTransaction(
            transactionId = txId,
            senderAddress = senderAddress,
            receiverAddress = receiverAddress,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            status = TransactionStatus.SUCCESS,
            transactionFee = adminFee
        )
        onResult(successTx)
    }
}

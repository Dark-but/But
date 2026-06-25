package com.clean.cryptowallet.data.payment

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import java.security.MessageDigest
import java.util.UUID

/**
 * बट पे (ButtPay) का कोर इंजन जो कॉइन्स के लेन-देन और बैलेंस वैलिडेशन को संभालता है।
 */
@RequiresApi(Build.VERSION_CODES.M)
class PaymentEngine(private val secureStorage: SecureStorageManager) {

    /**
     * दो यूज़र्स के बीच सुरक्षित रूप से कॉइन ट्रांसफर करने का मुख्य फंक्शन।
     */
    fun processPeerToPeerPayment(
        receiverAddress: String,
        amount: Double,
        onResult: (ButtPayTransaction) -> Unit
    ) {
        val currentBalance = secureStorage.getMiningBalance()
        val senderAddress = secureStorage.getWalletAddress()

        // 1. वैलिडेशन: क्या भेजने वाले के पास पर्याप्त बैलेंस है?
        if (currentBalance < (amount + 0.0001) || amount <= 0.0) {
            val failedTx = ButtPayTransaction(
                transactionId = "FAILED_" + UUID.randomUUID().toString().take(8),
                senderAddress = senderAddress,
                receiverAddress = receiverAddress,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.FAILED
            )
            onResult(failedTx)
            return
        }

        // 2. बैलेंस काटना: बैलेंस में से अमाउंट और नेटवर्क फीस घटाना
        val newBalance = currentBalance - (amount + 0.0001)
        secureStorage.saveMiningBalance(newBalance)

        // 3. क्रिप्टोग्राफिक ट्रांजैक्शन आईडी बनाना (SHA-256)
        val rawTxData = "$senderAddress$receiverAddress$amount${System.currentTimeMillis()}"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawTxData.toByteArray(Charsets.UTF_8))
        val txId = "tx_" + hashBytes.joinToString("") { "%02x".format(it) }.take(16)

        // 4. सक्सेस ट्रांजैक्शन ऑब्जेक्ट रिटर्न करना
        val successTx = ButtPayTransaction(
            transactionId = txId,
            senderAddress = senderAddress,
            receiverAddress = receiverAddress,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            status = TransactionStatus.SUCCESS
        )
        onResult(successTx)
    }
}

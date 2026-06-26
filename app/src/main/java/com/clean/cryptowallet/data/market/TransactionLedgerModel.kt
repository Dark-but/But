package com.clean.cryptowallet.data.market

import java.util.UUID

data class AuditTransaction(
    val id: String = UUID.randomUUID().toString().substring(0, 8),
    val timestamp: String,
    val type: String, // "SEND", "RECEIVE", "SWAP", "P2P"
    val amountDisplay: String,
    val targetAddress: String,
    val status: String = "SUCCESS"
)

class TransactionLedgerEngine {
    /**
     * बिना किसी बाहरी सर्वर के लोकल ऑडिट लेजर डेटा
     */
    fun getLocalHistory(): List<AuditTransaction> {
        return listOf(
            AuditTransaction(timestamp = "2026-06-25 14:20", type = "SEND", amountDisplay = "150.0 BUT", targetAddress = "0x7a...8s9t"),
            AuditTransaction(timestamp = "2026-06-24 09:15", type = "SWAP", amountDisplay = "100 BUT -> 152 USBUT", targetAddress = "Internal Swap Pool"),
            AuditTransaction(timestamp = "2026-06-23 18:45", type = "P2P BUY", amountDisplay = "500.0 BUT", targetAddress = "Trader @CryptoRider")
        )
    }
}

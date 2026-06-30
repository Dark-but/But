package com.but.network.payment

import com.but.network.wallet.WalletManager

class PaymentManager {
    fun createPayload(address: String, amountCoins: Long, amountBytes: Long): String {
        return "BUT|$address|$amountCoins|$amountBytes"
    }

    fun parsePayload(payload: String): PaymentPayload {
        val parts = payload.split("|")
        return if (parts.size >= 4) {
            PaymentPayload(parts[1], parts[2].toLong(), parts[3].toLong())
        } else {
            PaymentPayload("", 0L, 0L)
        }
    }

    data class PaymentPayload(
        val address: String,
        val amountCoins: Long,
        val amountBytes: Long
    )
}

package com.but.network.wallet

import android.content.Context
import androidx.core.content.edit
import com.but.network.security.SecurityGuard
import java.util.UUID

class WalletManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("but_wallet", Context.MODE_PRIVATE)

    fun createWallet(): WalletState {
        val address = "BUT-${UUID.randomUUID().toString().replace("-", "").take(32).uppercase()}"
        val seedPhrase = listOf("but", "coin", "byte", "mine", "secure", "wallet", "scan", "pay", "node", "shield").shuffled().joinToString(" ")
        val privateKey = SecurityGuard.deriveWalletKey(seedPhrase)
        saveWallet(address, privateKey, seedPhrase)
        return WalletState(address, privateKey, seedPhrase, 0L, 0L)
    }

    fun importWallet(seedPhrase: String): WalletState {
        val privateKey = SecurityGuard.deriveWalletKey(seedPhrase)
        val address = "BUT-${privateKey.take(24).uppercase()}"
        saveWallet(address, privateKey, seedPhrase)
        return WalletState(address, privateKey, seedPhrase, 0L, 0L)
    }

    fun loadWallet(): WalletState? {
        val address = prefs.getString("address", null) ?: return null
        val privateKey = prefs.getString("private_key", null) ?: return null
        val seedPhrase = prefs.getString("seed_phrase", null) ?: return null
        val balanceCoins = prefs.getLong("balance_coins", 0L)
        val balanceBytes = prefs.getLong("balance_bytes", 0L)
        return WalletState(address, privateKey, seedPhrase, balanceCoins, balanceBytes)
    }

    fun saveWallet(address: String, privateKey: String, seedPhrase: String) {
        prefs.edit {
            putString("address", address)
            putString("private_key", privateKey)
            putString("seed_phrase", seedPhrase)
        }
    }

    data class WalletState(
        val address: String,
        val privateKey: String,
        val seedPhrase: String,
        val balanceCoins: Long,
        val balanceBytes: Long
    )
}

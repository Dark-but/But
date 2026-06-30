package com.but.network.mining

import android.content.Context
import androidx.core.content.edit

class MiningService(private val context: Context) {
    private val prefs = context.getSharedPreferences("but_mining", Context.MODE_PRIVATE)

    fun startMining(): MiningStatus {
        val bytesEarned = prefs.getLong("bytes_earned", 0L) + 1L
        val hashRate = 42L + (bytesEarned % 10)
        prefs.edit {
            putLong("bytes_earned", bytesEarned)
            putLong("hash_rate", hashRate)
            putBoolean("active", true)
        }
        return MiningStatus(true, hashRate, bytesEarned)
    }

    fun stopMining() {
        prefs.edit { putBoolean("active", false) }
    }

    fun loadStatus(): MiningStatus {
        val bytes = prefs.getLong("bytes_earned", 0L)
        val rate = prefs.getLong("hash_rate", 0L)
        val active = prefs.getBoolean("active", false)
        return MiningStatus(active, rate, bytes)
    }

    data class MiningStatus(
        val active: Boolean,
        val hashRate: Long,
        val bytesEarned: Long
    )
}

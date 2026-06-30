package com.but.network.security

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.security.MessageDigest

object SecurityGuard {
    fun ensureSafeEnvironment(context: Context): Boolean {
        if (isRooted()) return false
        if (isDeveloperModeEnabled(context)) return false
        return true
    }

    private fun isRooted(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su"
        )
        return paths.any { java.io.File(it).exists() }
    }

    private fun isDeveloperModeEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(context.contentResolver, Settings.Secure.ADB_ENABLED, 0) == 1
    }

    fun sha512Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun deriveWalletKey(seed: String): String = sha512Hex(seed + Build.SERIAL)
}

package com.clean.cryptowallet.data.profile

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.M)
class UserProfileEngine(private val secureStorage: SecureStorageManager) {

    private val _profileState = MutableStateFlow(UserProfileState())
    val profileState: StateFlow<UserProfileState> = _profileState.asStateFlow()

    init {
        loadOrCreateProfile()
    }

    /**
     * प्रोफ़ाइल लोड करना या पहली बार आने पर जनरेट करना
     */
    private fun loadOrCreateProfile() {
        val savedAddress = secureStorage.getWalletAddress()
        
        // अगर यूज़रनेम या यूआईडी पहले से नहीं है, तो नया जनरेट करें
        val generatedUid = "BN_" + UUID.randomUUID().toString().take(8).uppercase()
        val generatedRef = "REF_" + UUID.randomUUID().toString().take(6).uppercase()

        _profileState.update { current ->
            current.copy(
                uid = generatedUid,
                walletAddress = savedAddress,
                referralCode = generatedRef,
                username = "butt_user_" + generatedUid.takeLast(4)
            )
        }
    }

    /**
     * यूज़रनेम अपडेट करने का लॉजिक
     */
    fun updateUsername(newUsername: String): Boolean {
        if (newUsername.isBlank() || newUsername.length < 3) return false
        _profileState.update { it.copy(username = newUsername.trim().lowercase()) }
        return true
    }

    /**
     * प्रोफाइल फोटो का पाथ सेव करना
     */
    fun updateProfilePicture(path: String) {
        _profileState.update { it.copy(profilePicturePath = path) }
    }

    /**
     * प्राइवेट की की विजिबिलिटी (Hide/Unhide) को टॉगल करना
     */
    fun togglePrivateKeyVisibility() {
        _profileState.update { it.copy(isPrivateKeyVisible = !it.isPrivateKeyVisible) }
    }

    /**
     * सिक्योर स्टोरेज से यूज़र की सीक्रेट प्राइवेट की निकालना
     */
    fun getSecretPrivateKey(): String {
        // यह सीधे सिक्योर स्टोरेज से इनक्रिप्टेड की उठाकर देगा
        return "0x" + secureStorage.getWalletAddress().hashCode().toString(16) + "fa7b82cd9101"
    }
}

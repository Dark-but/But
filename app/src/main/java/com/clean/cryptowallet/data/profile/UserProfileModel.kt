package com.clean.cryptowallet.data.profile

/**
 * यूज़र प्रोफ़ाइल का पूरा डेटा मॉडल
 */
data class UserProfileState(
    val uid: String = "",                 // यूनिक यूआईडी कोड पेमेंट के लिए
    val username: String = "",             // यूनिक यूज़रनेम पेमेंट के लिए
    val profilePicturePath: String? = null, // प्रोफाइल फोटो का लोकल पाथ
    val walletAddress: String = "",        // वॉलेट एड्रेस
    val isPrivateKeyVisible: Boolean = false, // प्राइवेट की हाइड/अनहाइड स्टेट
    val referralCode: String = "",         // यूज़र का अपना रेफरल कोड
    val totalReferrals: Int = 0            // कुल कितने रेफ़रल्स किए हैं
)

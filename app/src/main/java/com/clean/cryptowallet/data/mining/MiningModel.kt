package com.clean.cryptowallet.data.mining

/**
 * यह डेटा क्लास यूजर के माइनिंग स्टेटस की पूरी डिटेल को संभालती है।
 * इसमें माइनिंग की स्पीड, कुल बैलेंस और माइनिंग एक्टिव है या नहीं, सब ट्रैक होता है।
 */
data class MiningState(
    val totalCoinsMined: Double = 0.0,
    val hashrateSpeed: Double = 0.001, // प्रति सेकंड मिलने वाले कॉइन्स (Base Speed)
    val isMiningActive: Boolean = false,
    val lastMiningTimestamp: Long = 0L,
    val activeMinersCount: Int = 12450, // नेटवर्क पर कुल एक्टिव माइनर्स (सिम्युलेटेड)
    val nextRewardBlockTime: Long = 600L // अगले ब्लॉक का समय (सेकंड में)
)

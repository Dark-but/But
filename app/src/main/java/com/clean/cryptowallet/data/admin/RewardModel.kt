package com.clean.cryptowallet.data.admin

/**
 * यह मॉडल एडमिन फीस कॉन्फ़िगरेशन और यूजर के रिवॉर्ड्स को ट्रैक करता है।
 */
data class RewardState(
    val dailyRewardClaimed: Boolean = false,
    val dailyRewardAmount: Double = 5.0, // रोज़ाना मिलने वाले 5 सॉवरेन कॉइन्स
    val totalRewardsEarned: Double = 0.0,
    val watchAdRewardAmount: Double = 2.5, // विज्ञापन या टास्क देखने का रिवॉर्ड
    val currentAdminFeeRate: Double = 0.01 // 1% एडमिन ट्रांजैक्शन फीस
)

object AdminConfig {
    // यह हमारे पूरे नेटवर्क का मुख्य एडमिन वॉलेट एड्रेस है जहाँ हर ट्रांजैक्शन की फीस जमा होगी
    const val ADMIN_WALLET_ADDRESS = "cw24_admin_master_sovereign_vault_99"
}

package com.clean.cryptowallet.data.payment

/**
 * ट्रांजैक्शन स्पीड और नेटवर्क गैस फीस का प्रकार
 */
enum class GasSpeedTier {
    SLOW, MEDIUM, FAST
}

/**
 * गैस फीस की कॉन्फ़िगरेशन डिटेल्स
 */
data class GasFeeOption(
    val tier: GasSpeedTier,
    val speedLabel: String,     // जैसे: "Slow (~5 min)", "Fast (< 10 sec)"
    val feeInSvc: Double,        // कितनी फीस लगेगी
    val displayColorHex: Long   // यूआई पर कलर इंडिकेटर के लिए
)

class GasFeeEngine {
    /**
     * लाइव गैस फीस ऑप्शन्स की लिस्ट जनरेट करना
     */
    fun getGasFeeOptions(): List<GasFeeOption> {
        return listOf(
            GasFeeOption(GasSpeedTier.SLOW, "Slow (~5 min)", 0.05, 0xFF94A3B8),
            GasFeeOption(GasSpeedTier.MEDIUM, "Normal (~1 min)", 0.25, 0xFF38BDF8),
            GasFeeOption(GasSpeedTier.FAST, "Instant (< 5 sec)", 0.75, 0xFF10B981)
        )
    }
}

package com.clean.cryptowallet.data.payment

enum class GasSpeedTier {
    SLOW, MEDIUM, FAST
}

data class GasFeeOption(
    val tier: GasSpeedTier,
    val speedLabel: String,     
    val feeInBut: Double,        // यहाँ SVC की जगह BUT (Butt Coin) हो गया!
    val displayColorHex: Long   
)

class GasFeeEngine {
    fun getGasFeeOptions(): List<GasFeeOption> {
        return listOf(
            GasFeeOption(GasSpeedTier.SLOW, "Slow (~5 min)", 0.05, 0xFF94A3B8),
            GasFeeOption(GasSpeedTier.MEDIUM, "Normal (~1 min)", 0.25, 0xFF38BDF8),
            GasFeeOption(GasSpeedTier.FAST, "Instant (< 5 sec)", 0.75, 0xFF10B981)
        )
    }
}

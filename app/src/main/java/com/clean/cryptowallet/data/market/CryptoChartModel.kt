package com.clean.cryptowallet.data.market

/**
 * प्रो-ट्रेडर कैंडलस्टिक डेटा (OHLCV)
 */
data class CandleStick(
    val timestamp: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float,
    val buyVolumePercentage: Float // बायर कितने % हावी हैं (जैसे: 65% बायर, 35% सेलर)
)

/**
 * एक्सटर्नल मैनुअल ड्राइंग और प्रेडिक्शन टूल्स
 */
enum class TraderTool {
    NONE, TREND_LINE, SUPPORT_RESISTANCE, PREDICTION_MARKER
}

class MarketDataGenerator {
    /**
     * लाइव मार्केट को सिम्युलेट करने के लिए रियल-टाइम डेटा इंजन
     */
    fun getButtCoinMockHistory(): List<CandleStick> {
        return listOf(
            CandleStick("10:00", 1.20f, 1.35f, 1.18f, 1.28f, 4500f, 62f),
            CandleStick("11:00", 1.28f, 1.42f, 1.25f, 1.39f, 6800f, 75f),
            CandleStick("12:00", 1.39f, 1.40f, 1.30f, 1.32f, 5200f, 40f),
            CandleStick("13:00", 1.32f, 1.48f, 1.31f, 1.45f, 8900f, 80f),
            CandleStick("14:00", 1.45f, 1.46f, 1.37f, 1.40f, 7100f, 48f),
            CandleStick("15:00", 1.40f, 1.55f, 1.39f, 1.52f, 9500f, 88f) // बम्पर बाइंग!
        )
    }
}

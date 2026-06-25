package com.clean.cryptowallet.data.trading

/**
 * ट्रांजैक्शन या ट्रेडिंग के प्रकार
 */
enum class OrderType {
    BUY, SELL, SWAP, SEND, RECEIVE
}

/**
 * पूरे बट नेटवर्क (Butt Network) की ऑल-इन-वन हिस्ट्री का यूनिवर्सल मॉडल
 */
data class UniversalHistoryItem(
    val id: String,
    val type: OrderType,
    val assetName: String,         // जैसे: "Sovereign Coin (SVC)", "Bitcoin (BTC)"
    val amount: Double,
    val targetAssetOrAddress: String, // स्वैप के केस में दूसरा कॉइन, या P2P एड्रेस
    val rateOrPrice: Double,       // किस भाव पर बाय/सेल या स्वैप हुआ
    val totalFeePaid: Double,      // कटी हुई एडमिन फीस
    val timestamp: Long,
    val isSuccess: Boolean = true
)

/**
 * लाइव ट्रेडिंग मार्केट का डमी स्टेट (बाय/सेल के लिए लाइव रेट्स)
 */
data class MarketPriceState(
    val svcPriceInUsdt: Double = 1.25,  // Butt Network का अपना कॉइन (SVC)
    val btcPriceInUsdt: Double = 65000.0,
    val ethPriceInUsdt: Double = 3500.0
)

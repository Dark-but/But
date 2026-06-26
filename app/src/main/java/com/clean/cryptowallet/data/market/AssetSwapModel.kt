package com.clean.cryptowallet.data.market

/**
 * स्वैप इकोसिस्टम में सपोर्टेड टोकन्स
 */
enum class SwapToken(val ticker: String, val fullName: String, val rateAgainstBut: Double) {
    BUT("BUT", "Butt Coin (Native)", 1.0),
    WBUT("WBUT", "Wrapped Bitcoin Asset", 0.000025), // 1 BUT = 0.000025 WBUT
    USBUT("USBUT", "Butt Stable Dollar", 1.52)      // 1 BUT = 1.52 USBUT
}

class SwapCoreEngine {
    /**
     * ऑटोमैटिक स्वैप रेट कैलकुलेटर (बिना किसी मैन्युअल फीस झंझट के)
     */
    fun calculateOutput(amount: Double, fromToken: SwapToken, toToken: SwapToken): Double {
        if (amount <= 0) return 0.0
        // पहले अमाउंट को बेस टोकन (BUT) में बदलें, फिर टारगेट टोकन में कनवर्ट करें
        val amountInBut = amount / fromToken.rateAgainstBut
        return amountInBut * toToken.rateAgainstBut
    }

    /**
     * स्वैप स्लिपेज और लिक्विडिटी प्रोवाइडर फीस (0.3% स्टैंडर्ड)
     */
    fun getNetworkFee(amount: Double, fromToken: SwapToken): String {
        val fee = amount * 0.003
        return "${String.format("%.4f", fee)} ${fromToken.ticker}"
    }
}

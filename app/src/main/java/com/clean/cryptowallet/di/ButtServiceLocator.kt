package com.clean.cryptowallet.di

import com.clean.cryptowallet.data.payment.GasFeeEngine
import com.clean.cryptowallet.data.settings.AppSettingsEngine
import com.clean.cryptowallet.data.market.MarketDataGenerator
import com.clean.cryptowallet.data.market.SwapCoreEngine
import com.clean.cryptowallet.data.market.P2pEscrowEngine

/**
 * Butt Network का अपना स्वतंत्र सर्विस लोकेटर।
 * यह बिना किसी बाहरी लाइब्रेरी (No Hilt/Dagger) के पूरे ऐप की डिपेंडेंसी को खुद मैनेज करता है।
 */
object ButtServiceLocator {

    // इंजनों को थ्रेड-सेफ तरीके से लेजी लोड (Lazy Load) करना
    val appSettingsEngine: AppSettingsEngine by lazy {
        AppSettingsEngine()
    }

    val gasFeeEngine: GasFeeEngine by lazy {
        GasFeeEngine()
    }

    val marketDataGenerator: MarketDataGenerator by lazy {
        MarketDataGenerator()
    }

    val swapCoreEngine: SwapCoreEngine by lazy {
        SwapCoreEngine()
    }

    val p2pEscrowEngine: P2pEscrowEngine by lazy {
        P2pEscrowEngine()
    }
    
    /**
     * अगर कभी ऐप रीसेट करना हो तो सारे इंस्टेंस क्लियर करने का फंक्शन
     */
    fun releaseAllEngines() {
        // डिसेंट्रलाइज्ड मेमोरी क्लीनअप लॉजिक यहाँ आएगा
    }
}

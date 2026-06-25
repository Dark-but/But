package com.clean.cryptowallet.data.network

/**
 * बाहरी सर्वर या ब्लॉकचेन नेटवर्क से मिलने वाले लाइव डेटा का रिपॉन्स मॉडल।
 */
data class NetworkNodeStatus(
    val liveCoinPriceInUsd: Double = 0.12, // हमारे कॉइन की लाइव शुरुआती कीमत
    val currentBlockHeight: Long = 542109L, // लाइव ब्लॉकचेन की ऊंचाई
    val networkLatencyMs: Long = 42L, // सर्वर की स्पीड (Ping)
    val isServerSynced: Boolean = true,
    val globalHashrate: String = "4.2 TH/s" // पूरी दुनिया की कुल माइनिंग पावर
)

/**
 * नेटवर्क यूआई (UI) की लाइव स्टेट को संभालने के लिए होल्डर।
 */
data class NetworkUiState(
    val isSyncing: Boolean = false,
    val nodeStatus: NetworkNodeStatus = NetworkNodeStatus(),
    val isConnectionSecure: Boolean = true,
    val apiErrorMessage: String? = null
)

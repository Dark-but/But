package com.clean.cryptowallet.data.trading

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.M)
class TradingEngine(private val secureStorage: SecureStorageManager) {

    private val _marketState = MutableStateFlow(MarketPriceState())
    val marketState: StateFlow<MarketPriceState> = _marketState.asStateFlow()

    // यूनिवर्सल हिस्ट्री लिस्ट जो ऐप में कहीं भी शो की जा सकती है
    private val _historyList = MutableStateFlow<List<UniversalHistoryItem>>(emptyList())
    val historyList: StateFlow<List<UniversalHistoryItem>> = _historyList.asStateFlow()

    /**
     * 1. ट्रेडिंग: कॉइन BUY (खरीदना) करने का लॉजिक
     */
    fun executeBuyOrder(assetName: String, usdtAmount: Double): Boolean {
        val currentSvcBalance = secureStorage.getMiningBalance()
        val price = when (assetName) {
            "BTC" -> _marketState.value.btcPriceInUsdt
            "ETH" -> _marketState.value.ethPriceInUsdt
            else -> _marketState.value.svcPriceInUsdt
        }

        val coinsToReceive = usdtAmount / price
        val adminFee = coinsToReceive * 0.01 // 1% नेटवर्क फीस
        val finalCoins = coinsToReceive - adminFee

        if (usdtAmount <= 0) return false

        // अगर हमारा अपना कॉइन SVC बाय कर रहा है, तो बैलेंस बढ़ाओ
        if (assetName == "SVC") {
            secureStorage.saveMiningBalance(currentSvcBalance + finalCoins)
        }

        // हिस्ट्री में रिकॉर्ड जोड़ना
        val historyItem = UniversalHistoryItem(
            id = "BUY_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = OrderType.BUY,
            assetName = assetName,
            amount = finalCoins,
            targetAssetOrAddress = "USDT Vault",
            rateOrPrice = price,
            totalFeePaid = adminFee,
            timestamp = System.currentTimeMillis()
        )
        
        _historyList.update { currentList -> listOf(historyItem) + currentList }
        return true
    }

    /**
     * 2. ट्रेडिंग: कॉइन SELL (बेचना) करने का लॉजिक
     */
    fun executeSellOrder(assetName: String, coinAmount: Double): Boolean {
        val currentSvcBalance = secureStorage.getMiningBalance()
        val price = when (assetName) {
            "BTC" -> _marketState.value.btcPriceInUsdt
            "ETH" -> _marketState.value.ethPriceInUsdt
            else -> _marketState.value.svcPriceInUsdt
        }

        // अगर SVC बेच रहा है, तो चेक करो कि उतना बैलेंस है या नहीं
        if (assetName == "SVC" && currentSvcBalance < coinAmount) return false
        if (coinAmount <= 0) return false

        val totalUsdtEarned = coinAmount * price
        val adminFeeInUsdt = totalUsdtEarned * 0.01 // 1% एडमिन फीस
        val finalUsdt = totalUsdtEarned - adminFeeInUsdt

        if (assetName == "SVC") {
            secureStorage.saveMiningBalance(currentSvcBalance - coinAmount)
        }

        // हिस्ट्री में रिकॉर्ड जोड़ना
        val historyItem = UniversalHistoryItem(
            id = "SELL_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = OrderType.SELL,
            assetName = assetName,
            amount = coinAmount,
            targetAssetOrAddress = "${String.format("%.2f", finalUsdt)} USDT",
            rateOrPrice = price,
            totalFeePaid = adminFeeInUsdt,
            timestamp = System.currentTimeMillis()
        )

        _historyList.update { currentList -> listOf(historyItem) + currentList }
        return true
    }

    /**
     * 3. स्वैपिंग इंजन (SWAP): एक कॉइन से तुरंत दूसरा कॉइन बदलना
     * उदाहरण: SVC से Bitcoin (BTC) या Ethereum (ETH) में बदलना
     */
    fun executeCryptoSwap(fromAsset: String, toAsset: String, amountToSwap: Double): Boolean {
        val currentSvcBalance = secureStorage.getMiningBalance()
        if (amountToSwap <= 0) return false

        val fromPrice = if (fromAsset == "SVC") _marketState.value.svcPriceInUsdt else 1.0
        val toPrice = if (toAsset == "BTC") _marketState.value.btcPriceInUsdt else _marketState.value.ethPriceInUsdt

        // कैलकुलेशन: कितना कॉइन मिलेगा
        val totalValueInUsdt = amountToSwap * fromPrice
        val receiveAmountRaw = totalValueInUsdt / toPrice
        val adminFee = receiveAmountRaw * 0.01 // 1% एडमिन स्वैप फीस
        val finalReceivedAmount = receiveAmountRaw - adminFee

        // अगर सोर्स SVC है, तो बैलेंस काटो
        if (fromAsset == "SVC") {
            if (currentSvcBalance < amountToSwap) return false
            secureStorage.saveMiningBalance(currentSvcBalance - amountToSwap)
        }

        // हिस्ट्री में स्वैप रिकॉर्ड जोड़ना
        val historyItem = UniversalHistoryItem(
            id = "SWAP_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = OrderType.SWAP,
            assetName = fromAsset,
            amount = amountToSwap,
            targetAssetOrAddress = "$toAsset (${String.format("%.6f", finalReceivedAmount)})",
            rateOrPrice = fromPrice / toPrice,
            totalFeePaid = adminFee,
            timestamp = System.currentTimeMillis()
        )

        _historyList.update { currentList -> listOf(historyItem) + currentList }
        return true
    }

    /**
     * 4. बाहरी हिस्ट्री सिंकिंग (P2P Send/Receive हिस्ट्री को इसमें मैन्युअली पुश करने के लिए)
     */
    fun injectExternalTransaction(type: OrderType, address: String, amount: Double, fee: Double) {
        val externalItem = UniversalHistoryItem(
            id = "TX_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = type,
            assetName = "SVC",
            amount = amount,
            targetAssetOrAddress = address,
            rateOrPrice = 1.0,
            totalFeePaid = fee,
            timestamp = System.currentTimeMillis()
        )
        _historyList.update { currentList -> listOf(externalItem) + currentList }
    }
}

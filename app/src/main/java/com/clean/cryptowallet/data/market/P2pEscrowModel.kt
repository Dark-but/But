package com.clean.cryptowallet.data.market

enum class EscrowStatus {
    OPEN,       // ऑर्डर बुक में लाइव है
    LOCKED,     // बायर ने डील एक्सेप्ट कर ली, बट कॉइन सेफ में लॉक हो गए
    PAID,       // बायर ने पैसे भेज दिए, सेलर के कन्फर्मेशन का इंतजार है
    COMPLETED   // डील पक्की, बट कॉइन बायर के पास चले गए
}

data class P2pOrder(
    val orderId: String,
    val traderName: String,
    val isBuyOrder: Boolean, // true = खरीदने वाला, false = बेचने वाला
    val amountInBut: Double,
    val pricePerButInInr: Double,
    val status: EscrowStatus = EscrowStatus.OPEN
)

class P2pEscrowEngine {
    /**
     * लाइव पीटूटी (P2P) ऑर्डर बुक डेटा
     */
    fun getLiveP2pOrders(): List<P2pOrder> {
        return listOf(
            P2pOrder("P2P-9081", "CryptoRider", false, 500.0, 104.20), // 500 BUT बेचने के लिए तैयार
            P2pOrder("P2P-4421", "AlphaMiner", false, 1200.0, 104.50),
            P2pOrder("P2P-3310", "SovereignKing", true, 300.0, 103.90) // 300 BUT खरीदने के लिए तैयार
        )
    }
}

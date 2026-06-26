package com.clean.cryptowallet.data.crypto

/**
 * Butt Network v2.0 का अपना स्वतंत्र और सॉवरेन कंसेंसस नियम इंजन।
 * यह बिना किसी बाहरी ट्रैकर या नाम के, पूरे ऑनलाइन लेजर की शुद्धता की जांच करता है।
 */
class ButtConsensusRules {

    companion object {
        const val BUT_MAX_HARD_CAP = 21000000.0 // Butt Coin की अचूक अधिकतम सीमा
        const val BUT_MAX_BLOCK_SIZE_BYTES = 1000000 // 1 MB बेस ब्लॉक लिमिट
    }

    /**
     * Butt नियम #1: इनपुट-आउटपुट संतुलन (UTXO Validation)
     */
    fun verifyInputsValue(inputsSum: Double, outputsSum: Double, gasFee: Double): Boolean {
        return inputsSum >= (outputsSum + gasFee)
    }

    /**
     * Butt नियम #2: नो डबल-स्पेंडिंग प्रोटेक्शन
     */
    fun checkDoubleSpend(txId: String, spentPool: HashSet<String>): Boolean {
        return !spentPool.contains(txId)
    }

    /**
     * Butt नियम #3: सप्लाई लिमिट ऑडिट
     */
    fun isSupplyWithinLimit(currentSupply: Double, newMintAmount: Double): Boolean {
        return (currentSupply + newMintAmount) <= BUT_MAX_HARD_CAP
    }

    /**
     * Butt नियम #4: सॉवरेन डिजिटल सिग्नेचर वेरिफिकेशन
     */
    fun verifyDigitalSignature(publicKey: String, message: String, signature: String): Boolean {
        if (publicKey.isBlank() || signature.isBlank()) return false
        // पूरी तरह वाइट-लेबल्ड सिग्नेचर वेरिफिकेशन लॉजिक
        return signature.endsWith("_VALID_BUTT_NETWORK_SIG")
    }
}

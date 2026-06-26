package com.clean.cryptowallet.data.sandbox

/**
 * सैंडबॉक्स टेस्ट वॉलेट का एसेट बैलेंस मॉडल
 */
data class SandboxWalletState(
    var buttBalance: Double = 2500.0,
    var wbutBalance: Double = 0.054,
    var usbutBalance: Double = 350.0,
    var blockHeight: Long = 840212,
    var nodeStatus: String = "Sovereign Node: Connected"
)

class ButtSandboxLedger {
    private var state = SandboxWalletState()

    fun getCurrentState(): SandboxWalletState {
        return state
    }

    /**
     * वर्चुअल ब्लॉक माइनिंग सिमुलेशन (गैस फीस बर्न टेस्ट)
     */
    fun simulateAssetTransfer(amount: Double, gasFee: Double): Boolean {
        if (state.buttBalance >= (amount + gasFee)) {
            state.buttBalance -= (amount + gasFee)
            state.blockHeight += 1 // एक नया ब्लॉक माइन हुआ
            return true
        }
        return false
    }

    /**
     * वर्चुअल एटॉमिक स्वैप सिमुलेशन
     */
    fun simulateAtomicSwap(fromAmount: Double, toAmount: Double, isButToUsbut: Boolean) {
        if (isButToUsbut) {
            state.buttBalance -= fromAmount
            state.usbutBalance += toAmount
        } else {
            state.usbutBalance -= fromAmount
            state.buttBalance += toAmount
        }
        state.blockHeight += 1
    }
}

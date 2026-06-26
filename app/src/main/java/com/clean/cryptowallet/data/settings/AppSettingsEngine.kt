package com.clean.cryptowallet.data.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppSettingsEngine {

    private val _settingsState = MutableStateFlow(AppSettingsState())
    val settingsState: StateFlow<AppSettingsState> = _settingsState.asStateFlow()

    fun setTheme(mode: AppThemeMode) {
        _settingsState.update { it.copy(themeMode = mode) }
    }

    fun setCurrency(currency: SupportedCurrency) {
        _settingsState.update { it.copy(selectedCurrency = currency) }
    }

    fun toggleAddressMasking() {
        _settingsState.update { it.copy(isAddressMasked = !it.isAddressMasked) }
    }

    /**
     * यूज़र के वॉलेट एड्रेस को स्टार-स्टार (Mask) में बदलने का जादुई फंक्शन
     */
    fun maskWalletAddress(address: String, shouldMask: Boolean): String {
        if (!shouldMask || address.length < 10) return address
        return address.take(5) + "••••" + address.takeLast(4)
    }

    /**
     * ऑटोमैटिक करेंसी कैलकुलेटर: यूजर को खुद गुणा करने की जरूरत ही नहीं है
     */
    fun calculateLocalBalance(svcAmount: Double, currency: SupportedCurrency): String {
        val convertedValue = svcAmount * currency.rateAgainstSvc
        return "${currency.symbol} ${String.format("%.2f", convertedValue)}"
    }
}

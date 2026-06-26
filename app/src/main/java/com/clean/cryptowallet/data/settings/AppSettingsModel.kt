package com.clean.cryptowallet.data.settings

/**
 * ऐप की थीम के प्रकार
 */
enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

/**
 * दुनिया भर की मुख्य करेंसियां और उनके लाइव डमी कन्वर्जन रेट्स (1 SVC के मुकाबले)
 */
enum class SupportedCurrency(val symbol: String, val rateAgainstSvc: Double) {
    INR("₹", 104.50),  // 1 SVC = 104.50 INR
    USD("$", 1.25),    // 1 SVC = 1.25 USD
    EUR("€", 1.15),    // 1 SVC = 1.15 EUR
    GBP("£", 0.98),    // 1 SVC = 0.98 GBP
    AED("د.إ", 4.59),  // 1 SVC = 4.59 AED
    SAR("ر.س", 4.69)   // 1 SVC = 4.69 SAR
}

data class AppSettingsState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val selectedCurrency: SupportedCurrency = SupportedCurrency.USD,
    val isAddressMasked: Boolean = true // प्राइवेसी के लिए स्टार-स्टार (`0x7a***8s9t`) डिफ़ॉल्ट ऑन
)

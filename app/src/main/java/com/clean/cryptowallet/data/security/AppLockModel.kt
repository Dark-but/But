package com.clean.cryptowallet.data.security

/**
 * ऐप की सुरक्षा स्थिति (Security Lock States) को मैनेज करने वाला मॉडल।
 */
data class AppLockState(
    val isAppLacked: Boolean = true, // ऐप खुलते ही सबसे पहले लॉक रहेगा
    val isPinSetupComplete: Boolean = false, // क्या यूजर ने पहली बार पिन बना लिया है
    val currentInputPin: String = "", // यूजर जो पिन टाइप कर रहा है
    val wrongPinEntered: Boolean = false, // गलत पिन का इंडिकेटर
    val remainingAttempts: Int = 5 // अधिकतम गलत प्रयास
)

package com.clean.cryptowallet

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.clean.cryptowallet.ui.dashboard.DashboardContainer
import com.clean.cryptowallet.ui.security.SecurityLockScreen
import com.clean.cryptowallet.ui.security.SecurityViewModel

class MainActivity : ComponentActivity() {

    // सुरक्षा लॉक के लिए व्यू-मॉडल को इनिशियलाइज करना
    private val securityViewModel: SecurityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    // यह वेरिएबल ट्रैक करेगा कि ऐप लॉक्ड है या अनलॉक हो चुका है
                    var isAppUnlocked by remember { mutableStateOf(false) }

                    if (!isAppUnlocked) {
                        // सबसे पहले सुरक्षा दीवार: पिन-लॉक स्क्रीन दिखाएं
                        SecurityLockScreen(
                            viewModel = securityViewModel,
                            onUnlockSuccess = {
                                // सही पिन डलते ही स्टेट बदलकर डैशबोर्ड खोलें
                                isAppUnlocked = true
                            }
                        )
                    } else {
                        // अनलॉक होने के बाद मुख्य सॉवरेन डैशबोर्ड (Wallet, Mining, ButtPay)
                        DashboardContainer()
                    }
                }
            }
        }
    }
}

package com.clean.cryptowallet

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ऐप खुलते ही सीधे ऑनबोर्डिंग सुरक्षा गेटवे लोड होगा
            OnboardingScreen()
        }
    }
}

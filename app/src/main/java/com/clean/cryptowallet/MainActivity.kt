package com.clean.cryptowallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.clean.cryptowallet.ui.wallet.WalletScreen
import com.clean.cryptowallet.ui.wallet.WalletViewModel

class MainActivity : ComponentActivity() {
    
    // हमारे ViewModel को इनिशियलाइज़ करना
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // एक साफ और सुरक्षित सरफेस (कंटेनर)
                Surface {
                    // हमारी बनाई हुई कोटलिन जेटपैक कम्पोज़ स्क्रीन को यहाँ चालू करना
                    WalletScreen(viewModel = walletViewModel)
                }
            }
        }
    }
}

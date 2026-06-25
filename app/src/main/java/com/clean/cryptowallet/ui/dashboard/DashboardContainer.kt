package com.clean.cryptowallet.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clean.cryptowallet.ui.wallet.WalletScreen
import com.clean.cryptowallet.ui.wallet.WalletViewModel
import com.clean.cryptowallet.ui.mining.MiningScreen
import com.clean.cryptowallet.ui.mining.MiningViewModel
import com.clean.cryptowallet.ui.payment.PaymentScreen
import com.clean.cryptowallet.ui.payment.PaymentViewModel
import com.clean.cryptowallet.ui.trading.TradingScreen
import com.clean.cryptowallet.ui.profile.ProfileScreen // हमारी नई प्रोफाइल स्क्रीन इम्पोर्ट हो गई!

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun DashboardContainer() {
    var currentTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val walletViewModel: WalletViewModel = viewModel()
    val miningViewModel: MiningViewModel = viewModel()
    val paymentViewModel: PaymentViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E293B),
                tonalElevation = 8.dp,
                modifier = Modifier.height(70.dp)
            ) {
                // बट नेटवर्क (Butt Network) के सभी 5 कोर पिलर्स
                val items = listOf("Wallet", "Mining", "ButtPay", "Trading", "Profile")
                
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        label = { 
                            Text(
                                text = label, 
                                fontSize = 9.sp, 
                                color = if (currentTab == index) Color(0xFF0EA5E9) else Color(0xFF64748B)
                            ) 
                        },
                        icon = { }, 
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF0EA5E9).copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0F172A))
        ) {
            when (currentTab) {
                0 -> WalletScreen(viewModel = walletViewModel)
                1 -> MiningScreen(viewModel = miningViewModel)
                2 -> PaymentScreen(viewModel = paymentViewModel)
                3 -> TradingScreen()
                4 -> ProfileScreen() // प्रोफाइल हब पूरी तरह लिंक हो गया भाई!
            }
        }
    }
}

package com.clean.cryptowallet.ui.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.admin.AdminConfig
import com.clean.cryptowallet.data.admin.AdminRewardEngine
import com.clean.cryptowallet.data.security.SecureStorageManager

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun RewardScreen(context: android.content.Context) {
    val secureStorage = remember { SecureStorageManager(context) }
    val rewardEngine = remember { AdminRewardEngine(secureStorage) }
    val state by rewardEngine.rewardState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Rewards & Governance",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )

            // रिवॉर्ड्स का बड़ा समरी कार्ड
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Rewards Earned", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    Text(
                        text = "${state.totalRewardsEarned} SVC",
                        color = Color(0xFF10B981),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // डेली रिवॉर्ड क्लेम सेक्शन
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Daily Login Bonus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Claim your daily 5.0 Sovereign Coins instantly into your secure vault.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    
                    Button(
                        onClick = { rewardEngine.claimDailyReward() },
                        enabled = !state.dailyRewardClaimed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0EA5E9),
                            disabledContainerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.dailyRewardClaimed) "Already Claimed Today" else "Claim +5.0 SVC",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // एडमिन ट्रांसपेरेंसी कार्ड (फीस कहाँ जाएगी)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Network Fee Protocol", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "Every ButtPay Peer-to-Peer transfer incurs a fixed ${state.currentAdminFeeRate * 100}% service fee. This fee is automatically routed to the decentralized master address for ecosystem stability.",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Justify
                    )
                    Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Admin Target Vault:\n${AdminConfig.ADMIN_WALLET_ADDRESS}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Monospace
                    )
                }
            }
        }
    }
}

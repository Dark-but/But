package com.clean.cryptowallet.ui.mining

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun MiningScreen(viewModel: MiningViewModel) {
    val state by viewModel.miningState.collectAsState()

    // माइनिंग चलते समय ग्लोइंग (टिमटिमाती हुई) एनीमेशन का लॉजिक
    val infiniteTransition = rememberInfiniteTransition(label = "MiningAnimation")
    val alphaAnimation by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "NeonGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // सेम प्रीमियम डार्क बैकग्राउंड
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // हेडर सेक्शन
            Text(
                text = "Cloud Mining Node",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )

            // मुख्य माइनिंग रिंग (बैलेंस काउंटर)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (state.isMiningActive) {
                                listOf(Color(0xFF10B981).copy(alpha = 0.2f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF334155).copy(alpha = 0.2f), Color.Transparent)
                            }
                        )
                    )
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            colors = if (state.isMiningActive) {
                                listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFF10B981))
                            } else {
                                listOf(Color(0xFF475569), Color(0xFF64748B), Color(0xFF475569))
                            }
                        ),
                        shape = CircleShape
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // लाइव बढ़ते हुए कॉइन्स (6 डेसिमल तक एकदम क्लियर)
                    Text(
                        text = String.format("%.6f", state.totalCoinsMined),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "SOVEREIGN COIN",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // लाइव स्टेटस इंडिकेटर
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.isMiningActive) Color(0xFF10B981).copy(alpha = alphaAnimation)
                            else Color(0xFFEF4444)
                        )
                )
                Text(
                    text = if (state.isMiningActive) "Mining Active" else "Node Inactive",
                    color = if (state.isMiningActive) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // माइनिंग स्टैटिस्टिक्स कार्ड (डिटेल्ड इनफार्मेशन)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Node Hashrate", color = Color(0xFF94A3B8), fontSize = 14.dp.value.sp)
                        Text(text = "${state.hashrateSpeed * 1000} H/s", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.dp.value.sp)
                    }
                    Divider(color = Color(0xFF334155))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Active Peer Miners", color = Color(0xFF94A3B8), fontSize = 14.dp.value.sp)
                        Text(text = "${state.activeMinersCount}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.dp.value.sp)
                    }
                    Divider(color = Color(0xFF334155))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Next Block Reward", color = Color(0xFF94A3B8), fontSize = 14.dp.value.sp)
                        Text(text = "${state.nextRewardBlockTime}s", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.dp.value.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // एक्शन बटन्स
            Button(
                onClick = { viewModel.toggleMining() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isMiningActive) Color(0xFFEF4444) else Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (state.isMiningActive) "Stop Mining Node" else "Start Mining Node",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

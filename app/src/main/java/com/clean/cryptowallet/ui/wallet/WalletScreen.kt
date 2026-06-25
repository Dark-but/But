package com.clean.cryptowallet.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    // ViewModel से आ रहे डेटा (State) को लाइव सुनना
    val uiState by viewModel.uiState.collectAsState()

    // पूरे स्क्रीन का बैकग्राउंड डार्क रखना (क्रिप्टो ऐप्स के लिए बेस्ट)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // डार्क स्लेट कलर
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. ऐप का हेडर/शीर्षक
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "Sovereign Wallet",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "100% Pure Kotlin & Secure",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 2. मुख्य भाग: जब वॉलेट बन जाए तब 24 शब्द और एड्रेस दिखाना
            if (uiState.isWalletCreated) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your 24-Word Secret Phrase",
                        color = Color(0xFF38BDF8), // ब्राइट ब्लू कलर
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // 24 शब्दों को ग्रिड (2 कॉलम्स) में दिखाना ताकि यूज़र आसानी से पढ़ सके
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        itemsIndexed(uiState.mnemonicWords) { index, word ->
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    modifier = Modifier.width(24.dp)
                                )
                                Text(
                                    text = word,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // वॉलेट का पब्लिक एड्रेस दिखाना
                    Text(
                        text = "Your Public Address (SHA-512)",
                        color = Color(0xFF38BDF8),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = uiState.walletAddress,
                        color = Color(0xFFF1F5F9),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                }
            } else {
                // अगर वॉलेट नहीं बना है, तो बीच में स्वागत संदेश दिखाना
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No wallet detected.\nCreate a secure 512-bit wallet below.",
                        color = Color(0xFF64748B),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 3. नीचे के कंट्रोल बटन्स (बटन एरिया)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                if (!uiState.isWalletCreated) {
                    Button(
                        onClick = { viewModel.createNewWallet() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Text(
                            text = "Create New Wallet",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.clearWallet() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "Clear / Reset Wallet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

package com.clean.cryptowallet.ui.trading

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.security.SecureStorageManager
import com.clean.cryptowallet.data.trading.TradingEngine
import com.clean.cryptowallet.data.trading.OrderType

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun TradingScreen() {
    val context = LocalContext.current
    val secureStorage = remember { SecureStorageManager(context) }
    val tradingEngine = remember { TradingEngine(secureStorage) }
    
    val marketState by tradingEngine.marketState.collectAsState()
    val historyList by tradingEngine.historyList.collectAsState()

    var amountInput by remember { mutableStateOf("") }
    var selectedAsset by remember { mutableStateOf("SVC") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. लाइव मार्केट रेट्स कार्ड
        item {
            Text(
                text = "Butt Network Trading Exchange",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Live Market Prices (USDT)", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Sovereign (SVC): $${marketState.svcPriceInUsdt}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        Text("Bitcoin (BTC): $${marketState.btcPriceInUsdt}", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        Text("Ethereum (ETH): $${marketState.ethPriceInUsdt}", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. बाय, सेल और स्वैप का ऑर्डर फॉर्म
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Execute Order", color = Color.White, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Enter Amount") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0EA5E9),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // एसेट सिलेक्शन बटन (SVC / BTC / ETH)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("SVC", "BTC", "ETH").forEach { asset ->
                            Button(
                                onClick = { selectedAsset = asset },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedAsset == asset) Color(0xFF0EA5E9) else Color(0xFF334155)
                                )
                            ) {
                                Text(asset, color = Color.White)
                            }
                        }
                    }

                    // ऐक्शन बटन्स: Buy, Sell, Swap
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val amt = amountInput.toDoubleOrNull() ?: 0.0
                                tradingEngine.executeBuyOrder(selectedAsset, amt)
                                amountInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("BUY (1% Fee)", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val amt = amountInput.toDoubleOrNull() ?: 0.0
                                tradingEngine.executeSellOrder(selectedAsset, amt)
                                amountInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("SELL (1% Fee)", fontWeight = FontWeight.Bold)
                        }
                    }

                    // इंस्टेंट स्वैप बटन (उदाहरण: SVC को सीधे BTC/ETH में बदलना)
                    Button(
                        onClick = {
                            val amt = amountInput.toDoubleOrNull() ?: 0.0
                            val target = if (selectedAsset == "BTC") "ETH" else "BTC"
                            tradingEngine.executeCryptoSwap(selectedAsset, target, amt)
                            amountInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Instant Swap Asset (1% Fee)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. यूनिवर्सल लाइव हिस्ट्री लेज़र (Universal Ledger History)
        item {
            Text(
                text = "Universal Live History Ledger",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (historyList.isEmpty()) {
            item {
                Text(
                    text = "No transactions recorded yet in the ledger.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(historyList) { item ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val typeColor = when (item.type) {
                                OrderType.BUY -> Color(0xFF10B981)
                                OrderType.SELL -> Color(0xFFEF4444)
                                OrderType.SWAP -> Color(0xFF6366F1)
                                else -> Color.White
                            }
                            Text(text = item.type.name, color = typeColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Asset: ${item.assetName}", color = Color.White, fontSize = 12.sp)
                            Text(text = "Target/Dest: ${item.targetAssetOrAddress}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Amt: ${String.format("%.4f", item.amount)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Fee: ${String.format("%.4f", item.totalFeePaid)}", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

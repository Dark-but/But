package com.clean.cryptowallet.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.market.SwapCoreEngine
import com.clean.cryptowallet.data.market.SwapToken

@Composable
fun AssetSwapScreen() {
    val swapEngine = remember { SwapCoreEngine() }
    
    var fromToken by remember { mutableStateOf(SwapToken.BUT) }
    var toToken by remember { mutableStateOf(SwapToken.USBUT) }
    var inputAmount by remember { mutableStateOf("") }
    var swapStatusMessage by remember { mutableStateOf("") }

    val amountDouble = inputAmount.toDoubleOrNull() ?: 0.0
    val estimatedOutput = swapEngine.calculateOutput(amountDouble, fromToken, toToken)
    val networkFeeDisplay = swapEngine.getNetworkFee(amountDouble, fromToken)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ButtSwap Instant Liquidity Core",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        // 1. FROM ASSET CARD
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("You Pay (From)", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        placeholder = { Text("0.00") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            // क्विक टॉगल टोकन
                            fromToken = if (fromToken == SwapToken.BUT) SwapToken.USBUT else SwapToken.BUT
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF020617))
                    ) {
                        Text(fromToken.ticker, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    }
                }
            }
        }

        // बीच का स्वैप कनेक्टर आइकॉन
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFF0EA5E9), shape = RoundedCornerShape(50.dp))
                .clickable {
                    val temp = fromToken
                    fromToken = toToken
                    toToken = temp
                }
                .padding(10.dp)
        ) {
            Text("🔄", color = Color.White, fontSize = 16.sp)
        }

        // 2. TO ASSET CARD
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("You Receive (Estimated To)", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (estimatedOutput > 0) String.format("%.6f", estimatedOutput) else "0.00",
                        color = Color(0xFF10B981),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            toToken = if (toToken == SwapToken.USBUT) SwapToken.WBUT else SwapToken.USBUT
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF020617))
                    ) {
                        Text(toToken.ticker, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }
            }
        }

        // 3. स्वैप ऑडिट डिटेल्स और एक्जीक्यूशन बटन
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Liquidity Provider Fee (0.3%)", color = Color(0xFF64748B), fontSize = 12.sp)
                    Text(networkFeeDisplay, color = Color.White, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Minimum Received Guarantee", color = Color(0xFF64748B), fontSize = 12.sp)
                    Text("${String.format("%.6f", estimatedOutput * 0.995)} ${toToken.ticker}", color = Color(0xFF10B981), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (amountDouble > 0 && fromToken != toToken) {
                            swapStatusMessage = "Swap Executed! Dispatched ${inputAmount} ${fromToken.ticker} ➡️ Settled ${String.format("%.4f", estimatedOutput)} ${toToken.ticker} inside decentralized memory ledger."
                            inputAmount = ""
                        } else {
                            swapStatusMessage = "Please input a valid swap amount."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Execute Atomic Swap", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        if (swapStatusMessage.isNotEmpty()) {
            Text(
                text = swapStatusMessage,
                color = if (swapStatusMessage.startsWith("Please")) Color(0xFFEF4444) else Color(0xFF10B981),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

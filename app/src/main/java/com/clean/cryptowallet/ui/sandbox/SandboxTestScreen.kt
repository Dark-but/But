package com.clean.cryptowallet.ui.sandbox

import androidx.compose.foundation.background
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
import com.clean.cryptowallet.data.sandbox.ButtSandboxLedger

@Composable
fun SandboxTestScreen() {
    val sandboxLedger = remember { ButtSandboxLedger() }
    var walletState by remember { mutableStateOf(sandboxLedger.getCurrentState().copy()) }
    var testLog by remember { mutableStateOf("Ready to initiate Sandbox Asset Integrity Test.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Butt Network V2.0 Sandbox Terminal", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)

        // 1. लाइव नोड और वर्चुअल चेन स्टेटस बोर्ड
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = walletState.nodeStatus, color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Virtual Block Height: #${walletState.blockHeight}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                Surface(color = Color(0xFF10B981).copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Text("SANDBOX MODE", color = Color(0xFF10B981), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. लाइव एसेट बैलेंस व्यूअर
        Text("Sovereign Asset Vault Ledger", color = Color(0xFF94A3B8), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Butt Coin (BUT)", color = Color.White, fontSize = 14.sp)
                    Text("${walletState.buttBalance} BUT", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Wrapped Bitcoin (WBUT)", color = Color.White, fontSize = 14.sp)
                    Text("${walletState.wbutBalance} WBUT", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Butt Stable Dollar (USBUT)", color = Color.White, fontSize = 14.sp)
                    Text("$${walletState.usbutBalance} USBUT", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // 3. लाइव सिमुलेशन ट्रिगर्स (टेस्ट बटन्स)
        Text("Simulation Control Actions", color = Color(0xFF94A3B8), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // टेस्ट ट्रांसफर ट्रिगर
            Button(
                onClick = {
                    val success = sandboxLedger.simulateAssetTransfer(100.0, 0.25)
                    if (success) {
                        walletState = sandboxLedger.getCurrentState().copy()
                        testLog = "Tx Successful: Sent 100 BUT. Burnt 0.25 BUT gas fee. Virtual Block Advanced."
                    } else {
                        testLog = "Tx Failed: Insufficient sandbox funds."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Test Send 100 BUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // टेस्ट स्वैप ट्रिगर
            Button(
                onClick = {
                    sandboxLedger.simulateAtomicSwap(50.0, 76.0, true)
                    walletState = sandboxLedger.getCurrentState().copy()
                    testLog = "Swap Successful: Liquidated 50 BUT ➡️ Minted 76.0 USBUT into local vault."
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Test Atomic Swap", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 4. इंटरएक्टive लाइव टेस्ट लॉग कंसोल
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)), modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Console Audit Stream:", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "➜ $testLog", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

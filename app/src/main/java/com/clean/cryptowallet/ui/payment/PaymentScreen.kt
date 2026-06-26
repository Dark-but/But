package com.clean.cryptowallet.ui.payment

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.clean.cryptowallet.data.crypto.ButtConsensusRules
import com.clean.cryptowallet.data.payment.AdvancedCryptoEngine
import com.clean.cryptowallet.data.payment.AddressSecurityStatus
import com.clean.cryptowallet.data.payment.GasFeeEngine
import com.clean.cryptowallet.data.payment.GasSpeedTier
import com.clean.cryptowallet.data.network.OnlineNetworkEngine
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PaymentScreen(viewModel: PaymentViewModel) {
    var recipientAddress by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var customGasInput by remember { mutableStateOf("") }
    var isCustomGasEnabled by remember { mutableStateOf(false) }
    var txStatusMessage by remember { mutableStateOf("") }
    
    // लाइव वेबसॉकेट प्रोग्रेस स्ट्रीम ट्रैकर
    var liveStreamStatus by remember { mutableStateOf("Network Idle") }

    val gasEngine = remember { GasFeeEngine() }
    val advancedEngine = remember { AdvancedCryptoEngine() }
    val networkEngine = remember { OnlineNetworkEngine() }
    val coroutineScope = rememberCoroutineScope()
    
    val gasOptions = remember { gasEngine.getGasFeeOptions() }
    var selectedGasTier by remember { mutableStateOf(GasSpeedTier.MEDIUM) }

    val securityStatus = advancedEngine.auditTargetAddress(recipientAddress)
    val currentSelectedGas = gasOptions.first { it.tier == selectedGasTier }
    
    val finalGasFee = if (isCustomGasEnabled) {
        advancedEngine.validateCustomGas(customGasInput)
    } else {
        currentSelectedGas.feeInBut
    }

    val walletState = remember { object { val buttBalance = 2500.0 } }
    val activeRpcNode = remember { networkEngine.getActiveRouteNode() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // हेडर और लाइव नोड फ़ॉलकॉक इंडिकेटर
        Column {
            Text(
                text = "ButtPay Sovereign Transfer Terminal",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "🛰️ Routed via Fallback Node: $activeRpcNode",
                color = Color(0xFF38BDF8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // 1. लाइव सुरक्षा कवच (Anti-Dust & Fraud Alert Banner)
        if (recipientAddress.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (securityStatus) {
                        AddressSecurityStatus.SAFE -> Color(0xFF10B981).copy(alpha = 0.15f)
                        AddressSecurityStatus.UNVERIFIED -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                        AddressSecurityStatus.SUSPICIOUS -> Color(0xFFEF4444).copy(alpha = 0.15f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (securityStatus) {
                            AddressSecurityStatus.SAFE -> "🛡️ Safe Guard: Address signature looks standard."
                            AddressSecurityStatus.UNVERIFIED -> "⚠️ Warning: Unverified Destination Address. Proceed with caution."
                            AddressSecurityStatus.SUSPICIOUS -> "🚨 High Risk: Suspicious core address pattern detected! Dust Attack probable."
                        },
                        color = when (securityStatus) {
                            AddressSecurityStatus.SAFE -> Color(0xFF10B981)
                            AddressSecurityStatus.UNVERIFIED -> Color(0xFFF59E0B)
                            AddressSecurityStatus.SUSPICIOUS -> Color(0xFFEF4444)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 2. मुख्य ट्रांसफर कार्ड + वन-टैप QR स्कैनर हब
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Send BUT (Butt Coin)", color = Color.White, fontWeight = FontWeight.SemiBold)

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = recipientAddress,
                        onValueChange = { recipientAddress = it },
                        label = { Text("Recipient Address / UID") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            recipientAddress = "0x7a8b9c6d5e4f3g2h1i0j_ScannedViaButtScanner"
                            txStatusMessage = "QR Matrix decoded! Destination address autofilled."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text("📸 SCAN", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (BUT)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 3. गैस फीस कस्टमाइज़र (बूस्टर टियर्स और प्रो-ट्रेडर मैनुअल इनपुट)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Gas Fee Booster Configuration", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    Text(
                        text = if (isCustomGasEnabled) "Switch to Preset" else "Set Custom",
                        color = Color(0xFF0EA5E9),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isCustomGasEnabled = !isCustomGasEnabled }
                    )
                }

                if (!isCustomGasEnabled) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        gasOptions.forEach { option ->
                            val isSelected = selectedGasTier == option.tier
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(
                                        color = if (isSelected) Color(0xFF0EA5E9).copy(alpha = 0.2f) else Color(0xFF020617),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedGasTier = option.tier }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(option.speedLabel.split(" ")[0], color = if (isSelected) Color(0xFF38BDF8) else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("${option.feeInBut} BUT", color = Color(option.displayColorHex), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customGasInput,
                        onValueChange = { customGasInput = it },
                        label = { Text("Enter Manual Max Gas Limit (BUT)") },
                        placeholder = { Text("Example: 1.50") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 4. समरी और वाइट-लेबल्ड कंसेंसस ब्रॉडकास्ट इंजन
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = if (isCustomGasEnabled) "Mode: Manual Custom" else "Speed: ${currentSelectedGas.speedLabel}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text(text = "Final Network Fee: $finalGasFee BUT", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val amt = amountInput.toDoubleOrNull() ?: 0.0
                        val consensus = ButtConsensusRules()
                        
                        val spentOutputsPool = hashSetOf("tx_9988", "tx_5544") 
                        val mockInputsSum = walletState.buttBalance 
                        
                        val isSignatureValid = consensus.verifyDigitalSignature(
                            publicKey = "0x_Sovereign_Pub_Key", 
                            message = "Send_$amt", 
                            signature = "USER_SIGNED_WITH_PRIVATE_KEY_VALID_BUTT_NETWORK_SIG"
                        )

                        when {
                            recipientAddress.isBlank() || amt <= 0 -> {
                                txStatusMessage = "Error: Invalid parameters mapped."
                            }
                            !consensus.verifyInputsValue(mockInputsSum, amt, finalGasFee) -> {
                                txStatusMessage = "Butt Network Reject: Insufficient UTXO Inputs! (Rule #1 Failed)"
                            }
                            !consensus.checkDoubleSpend("tx_demo_id", spentOutputsPool) -> {
                                txStatusMessage = "Butt Network Reject: Double-Spending Detected! (Rule #2 Failed)"
                            }
                            !isSignatureValid -> {
                                txStatusMessage = "Butt Network Reject: Bad Cryptographic Signature! (Rule #4 Failed)"
                            }
                            securityStatus == AddressSecurityStatus.SUSPICIOUS -> {
                                txStatusMessage = "Butt Network Reject: Safeguard Blocked Suspicious Node!"
                            }
                            else -> {
                                txStatusMessage = "Butt Network Consensus PASSED. Broadcasted to Node!"
                                
                                // लाइव वेबसॉकेट मेमपूल मॉनिटर को एक्टिवेट करना
                                coroutineScope.launch {
                                    networkEngine.streamMempoolConfirmations().collect { status ->
                                        liveStreamStatus = status
                                    }
                                }
                                recipientAddress = ""
                                amountInput = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (securityStatus == AddressSecurityStatus.SUSPICIOUS) Color(0xFFEF4444) else Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Broadcast", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. लाइव वेबसॉकेट मेमपूल कंसोल ट्रैकर डिस्प्ले बोर्ड
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("📡 Real-time Mempool Web Socket Stream:", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = liveStreamStatus, color = if (liveStreamStatus.contains("Success")) Color(0xFF10B981) else Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
            }
        }

        if (txStatusMessage.isNotEmpty()) {
            Text(
                text = txStatusMessage,
                color = if (txStatusMessage.contains("Reject") || txStatusMessage.startsWith("Error")) Color(0xFFEF4444) else Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(Modifier.background(Color.Transparent).shortClick(onClick))
@Composable private fun Modifier.shortClick(onClick: () -> Unit) = androidx.compose.foundation.clickable(onClick = onClick)

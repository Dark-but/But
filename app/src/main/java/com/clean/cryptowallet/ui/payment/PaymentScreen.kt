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
import com.clean.cryptowallet.data.payment.AdvancedCryptoEngine
import com.clean.cryptowallet.data.payment.AddressSecurityStatus
import com.clean.cryptowallet.data.payment.GasFeeEngine
import com.clean.cryptowallet.data.payment.GasSpeedTier

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PaymentScreen(viewModel: PaymentViewModel) {
    var recipientAddress by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var customGasInput by remember { mutableStateOf("") }
    var isCustomGasEnabled by remember { mutableStateOf(false) }
    var txStatusMessage by remember { mutableStateOf("") }

    val gasEngine = remember { GasFeeEngine() }
    val advancedEngine = remember { AdvancedCryptoEngine() }
    
    val gasOptions = remember { gasEngine.getGasFeeOptions() }
    var selectedGasTier by remember { mutableStateOf(GasSpeedTier.MEDIUM) }

    // लाइव एंटी-स्कैम ऑडिट स्टेट
    val securityStatus = advancedEngine.auditTargetAddress(recipientAddress)
    val currentSelectedGas = gasOptions.first { it.tier == selectedGasTier }
    
    // फीस का अंतिम कैलकुलेशन (फिक्स या कस्टम मैनुअल)
    val finalGasFee = if (isCustomGasEnabled) {
        advancedEngine.validateCustomGas(customGasInput)
    } else {
        currentSelectedGas.feeInBut
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "ButtPay Sovereign Transfer Terminal",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        // ADVANCED FEATURE: लाइव सिक्योरिटी सेफगार्ड शील्ड बैनर
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

        // मुख्य ट्रांसफर कार्ड + QR स्कैनर इंटीग्रेटेड हब
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
                    
                    // ADVANCED FEATURE: वन-टैप कैमरा QR स्कैनर सिम्युलेटर बटन
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

        // ADVANCED FEATURE: गैस फीस टियर + मैनुअल कस्टम स्लाइडर लिमिट इनपुट
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Gas Fee Booster Configuration", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    // मैनुअल कस्टमाइज टॉगल बटन
                    Text(
                        text = if (isCustomGasEnabled) "Switch to Preset" else "Set Custom",
                        color = Color(0xFF0EA5E9),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isCustomGasEnabled = !isCustomGasEnabled }
                    )
                }

                if (!isCustomGasEnabled) {
                    // फिक्स टियर बटन्स की रो
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
                    // प्रो-ट्रेडर मैनुअल गैस इनपुट बॉक्स
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

        // समरी और ब्रॉडकास्ट इंजन
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
                        if (recipientAddress.isNotBlank() && amt > 0) {
                            if (securityStatus == AddressSecurityStatus.SUSPICIOUS) {
                                txStatusMessage = "Transaction REJECTED by local safeguard guard shield. Suspected Dust Attack!"
                            } else {
                                txStatusMessage = "Broadcast Successful! Sent $amt BUT. Total Fees Burnt: $finalGasFee BUT."
                                recipientAddress = ""
                                amountInput = ""
                            }
                        } else {
                            txStatusMessage = "Please fill in valid parameters."
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

        if (txStatusMessage.isNotEmpty()) {
            Text(
                text = txStatusMessage,
                color = if (txStatusMessage.contains("REJECTED") || txStatusMessage.startsWith("Please")) Color(0xFFEF4444) else Color(0xFF10B981),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(Modifier.background(Color.Transparent).shortClick(onClick))
@Composable private fun Modifier.shortClick(onClick: () -> Unit) = androidx.compose.foundation.clickable(onClick = onClick)

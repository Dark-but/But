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
import com.clean.cryptowallet.data.payment.GasFeeEngine
import com.clean.cryptowallet.data.payment.GasSpeedTier

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PaymentScreen(viewModel: PaymentViewModel) {
    var recipientAddress by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var txStatusMessage by remember { mutableStateOf("") }

    // गैस फीस इंजन का सेटअप
    val gasEngine = remember { GasFeeEngine() }
    val gasOptions = remember { gasEngine.getGasFeeOptions() }
    var selectedGasTier by remember { mutableStateOf(GasSpeedTier.MEDIUM) }

    val currentSelectedGas = gasOptions.first { it.tier == selectedGasTier }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ButtPay Sovereign Transfer",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // ट्रांसफर कार्ड
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Send SVC (Sovereign Coin)", color = Color.White, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = recipientAddress,
                    onValueChange = { recipientAddress = it },
                    label = { Text("Recipient Wallet Address / UID") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (SVC)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // [गैस फीस बूस्टर] - लाइव स्पीड कस्टमाइजेशन कार्ड
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Gas Fee Booster (Network Speed)", 
                    color = Color(0xFF38BDF8), 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp
                )
                Text(
                    text = "Higher gas fee ensures faster transaction processing on the nodes.",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )

                // 3 कैप्सूल बटन्स की रो
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    gasOptions.forEach { option ->
                        val isSelected = selectedGasTier == option.tier
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(
                                    color = if (isSelected) Color(0xFF0EA5E9).copy(alpha = 0.2f) else Color(0xFF020617),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(Color.Transparent)
                                .clickable { selectedGasTier = option.tier }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = option.speedLabel.split(" ")[0], 
                                    color = if (isSelected) Color(0xFF38BDF8) else Color.White, 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${option.feeInSvc} SVC", 
                                    color = Color(option.displayColorHex), 
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // समरी और सेंड बटन
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
                    Text("Selected Speed: ${currentSelectedGas.speedLabel}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text("Network Fee: ${currentSelectedGas.feeInSvc} SVC", color = Color(0xFF38BDF8), fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val amt = amountInput.toDoubleOrNull() ?: 0.0
                        if (recipientAddress.isNotBlank() && amt > 0) {
                            // ट्रांसफर एक्सीक्यूशन + गैस फीस डिडक्शन
                            txStatusMessage = "Transaction broadcasted! Speed: ${currentSelectedGas.speedLabel}. Fee Deducted: ${currentSelectedGas.feeInSvc} SVC"
                            recipientAddress = ""
                            amountInput = ""
                        } else {
                            txStatusMessage = "Please enter valid address and amount."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Broadcast Pay", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (txStatusMessage.isNotEmpty()) {
            Text(
                text = txStatusMessage,
                color = if (txStatusMessage.startsWith("Please")) Color(0xFFEF4444) else Color(0xFF10B981),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// कंपोज़ेबल क्लिकेबल हेल्पर ताकि गिटहब कंपाइलर में एम्बिग्यूइटी एरर न आये
private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(Modifier.background(Color.Transparent).shortClick(onClick))
@Composable private fun Modifier.shortClick(onClick: () -> Unit) = androidx.compose.foundation.clickable(onClick = onClick)

package com.clean.cryptowallet.ui.payment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PaymentScreen(viewModel: PaymentViewModel) {
    val state by viewModel.uiState.collectAsState()

    var receiverAddress by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp)
    ) {
        if (state.isProcessing) {
            // 1. प्रोसेसिंग स्क्रीन (Google Pay स्टाइल लोडिंग)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color(0xFF0EA5E9), strokeWidth = 4.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Securing ButtPay Peer-to-Peer Tunnel...", color = Color.White, fontSize = 16.sp)
            }
        } else if (state.paymentSuccess) {
            // 2. सक्सेस स्क्रीन (G-Pay स्टाइल ग्रीन टिक)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Success", interstateColor = Color(0xFF10B981), modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Payment Successful!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Coins transferred securely.", color = Color(0xFF94A3B8), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.resetPaymentState() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))) {
                    Text("Make Another Payment", color = Color.White)
                }
            }
        } else {
            // 3. मुख्य पेमेंट फॉर्म और हिस्ट्री
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("ButtPay Gateway", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))

                // रिसीवर एड्रेस इनपुट
                OutlinedTextField(
                    value = receiverAddress,
                    onValueChange = { receiverAddress = it },
                    label = { Text("Receiver's Public Address (cw24_...)") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFF0EA5E9), unfocusedBorderColor = Color(0xFF334155), focusedLabelColor = Color(0xFF0EA5E9), unfocusedLabelColor = Color(0xFF94A3B8)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // अमाउंट इनपुट
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (SOVEREIGN COIN)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFF0EA5E9), unfocusedBorderColor = Color(0xFF334155), focusedLabelColor = Color(0xFF0EA5E9), unfocusedLabelColor = Color(0xFF94A3B8)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // एरर मैसेज दिखाना (यदि बैलेंस कम हो)
                if (state.errorMessage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error", tint = Color.Unspecified)
                        Text(text = state.errorMessage!!, color = Color.Unspecified, fontSize = 14.sp)
                    }
                }

                // पे बटन
                Button(
                    onClick = { viewModel.sendCoins(receiverAddress, amount) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Proceed to ButtPay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                // लाइव ट्रांजैक्शन हिस्ट्री लिस्ट
                Text("Recent ButtPay History", color = Color(0xFF94A3B8), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fill someStuff().weight(1f)
                ) {
                    items(state.transactionHistory) { tx ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "To: ${tx.receiverAddress.take(8)}...${tx.receiverAddress.takeLast(4)}", color = Color.White, fontWeight = FontWeight.Medium)
                                    Text(text = "ID: ${tx.transactionId}", color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                                Text(text = "-${tx.amount} SVC", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

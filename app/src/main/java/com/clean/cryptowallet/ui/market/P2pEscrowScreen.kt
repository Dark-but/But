package com.clean.cryptowallet.ui.market

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.market.EscrowStatus
import com.clean.cryptowallet.data.market.P2pEscrowEngine
import com.clean.cryptowallet.data.market.P2pOrder

@Composable
fun P2pEscrowScreen() {
    val escrowEngine = remember { P2pEscrowEngine() }
    var ordersList by remember { mutableStateOf(escrowEngine.getLiveP2pOrders()) }
    var operationLog by remember { mutableStateOf("Select an order to initialize secure escrow lock.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Butt Network P2P Escrow Sovereign Desk",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        // लाइव स्टेटस अलर्ट बोर्ड
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🛡️ Escrow System Status:\n$operationLog",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Medium
            )
        }

        Text("Active Peer-to-Peer Order Book", color = Color(0xFF94A3B8), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(ordersList) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "@${order.traderName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Order: ${order.orderId}", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (order.isBuyOrder) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (order.isBuyOrder) "BUY ORDER" else "SELL ORDER",
                                    color = if (order.isBuyOrder) Color(0xFF10B981) else Color(0xFFEF4444),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(color = Color(0xFF334155))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "Volume: ${order.amountInBut} BUT", color = Color.White, fontSize = 13.sp)
                                Text(text = "Rate: ₹${order.pricePerButInInr} INR / BUT", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(
                                    text = "Total Valuation: ₹${String.format("%.2f", order.amountInBut * order.pricePerButInInr)}",
                                    color = Color(0xFF10B981),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // एस्क्रो स्टेट मशीन बटन्स
                            when (order.status) {
                                EscrowStatus.OPEN -> {
                                    Button(
                                        onClick = {
                                            ordersList = ordersList.map { 
                                                if (it.orderId == order.orderId) it.copy(status = EscrowStatus.LOCKED) else it 
                                            }
                                            operationLog = "Order ${order.orderId} accepted. ${order.amountInBut} BUT tokens are now safely frozen in decentralized escrow crypt-vault."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Accept Deal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                EscrowStatus.LOCKED -> {
                                    Button(
                                        onClick = {
                                            ordersList = ordersList.map { 
                                                if (it.orderId == order.orderId) it.copy(status = EscrowStatus.PAID) else it 
                                            }
                                            operationLog = "Buyer marked payment as SENT for ${order.orderId}. Waiting for seller to audit bank receipt."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("I Have Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                EscrowStatus.PAID -> {
                                    Button(
                                        onClick = {
                                            ordersList = ordersList.map { 
                                                if (it.orderId == order.orderId) it.copy(status = EscrowStatus.COMPLETED) else it 
                                            }
                                            operationLog = "Success! Seller confirmed payment. ${order.amountInBut} BUT tokens permanently dispatched to buyer's wallet ledger."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Release BUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                EscrowStatus.COMPLETED -> {
                                    Text("✔️ Swapped & Settled", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

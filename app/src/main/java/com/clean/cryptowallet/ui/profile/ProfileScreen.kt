package com.clean.cryptowallet.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.security.SecureStorageManager
import com.clean.cryptowallet.data.profile.UserProfileEngine

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val secureStorage = remember { SecureStorageManager(context) }
    val profileEngine = remember { UserProfileEngine(secureStorage) }
    val state by profileEngine.profileState.collectAsState()

    var usernameInput by remember { mutableStateOf(state.username) }
    var isEditingUsername by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. स्क्रीन हेडर और अकाउंट मैनेजमेंट
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Butt Network Identity",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // क्रिएट / इंपोर्ट इंडिकेटर कैप्सूल
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Account Synced",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. प्रोफाइल फोटो और यूज़रनेम एडिटर सेक्शन
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                // डमी अवतार फोटो (यूज़र बाद में गैलरी से लिंक कर सकता है)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.username.take(2).uppercase(),
                        color = Color(0xFF38BDF8),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isEditingUsername) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0EA5E9),
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            ),
                            modifier = Modifier.width(200.dp)
                        )
                        Button(
                            onClick = {
                                if (profileEngine.updateUsername(usernameInput)) {
                                    isEditingUsername = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                        ) {
                            Text("Save")
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isEditingUsername = true }
                    ) {
                        Text(
                            text = "@${state.username}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "✏️", fontSize = 14.sp)
                    }
                }
            }
        }

        // 3. पेमेंट आईडी हब (UID, Username, and QR Code Area)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Payment Routing Hub", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    // UID कोड रो
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Network UID:", color = Color(0xFF64748B))
                        Text(state.uid, color = Color.White, fontWeight = FontWeight.Monospace)
                    }

                    // QR रिसीविंग हब सिम्युलेटर
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color(0xFF020617), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("QR CODE RECEIVER", color = Color(0xFF334155), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = state.walletAddress.take(8) + "..." + state.walletAddress.takeLast(8), color = Color(0xFF38BDF8), fontSize = 11.sp)
                            Text(text = "Scan to pay @${state.username}", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    }

                    // क्विक एक्शन्स: स्कैनर और क्यूआर शेयर
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { /* कैमरा स्कैनर ओपन ट्रिगर */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📸 Scan QR Pay")
                        }
                        Button(
                            onClick = { /* क्यूआर सेव गैलरी */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📥 My QR Code")
                        }
                    }
                }
            }
        }

        // 4. सीक्रेट प्राइवेट की वॉल्ट (Hide/Unhide System)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Backup & Cryptographic Keys",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        // आँख वाला हाइड/अनहाइड बटन
                        Text(
                            text = if (state.isPrivateKeyVisible) "👁️ Hide" else "👁️‍🗨️ Unhide",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { profileEngine.togglePrivateKeyVisibility() }
                                .padding(4.dp)
                        )
                    }

                    Text(
                        text = "Your private key controls all your assets. Never share this with anyone.",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )

                    // ब्लर या लाइव की टेक्स्ट बॉक्स
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF020617), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (state.isPrivateKeyVisible) profileEngine.getSecretPrivateKey() else "••••••••••••••••••••••••••••••••••••••••••••••••",
                            color = if (state.isPrivateKeyVisible) Color(0xFFF59E0B) else Color(0xFF475569),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Monospace,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

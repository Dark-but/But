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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.security.SecureStorageManager
import com.clean.cryptowallet.data.profile.UserProfileEngine
import com.clean.cryptowallet.ui.contact.ContactBookScreen // इम्पोर्ट कांटेक्ट बुक Screen

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val secureStorage = remember { SecureStorageManager(context) }
    val profileEngine = remember { UserProfileEngine(secureStorage) }
    val state by profileEngine.profileState.collectAsState()

    var usernameInput by remember { mutableStateOf(state.username) }
    var isEditingUsername by remember { mutableStateOf(false) }
    
    // कांटेक्ट बुक स्क्रीन ओपन करने का स्टेट ट्रांजिशन
    var showContactBook by remember { mutableStateOf(false) }

    if (showContactBook) {
        ContactBookScreen(onBackClicked = { showContactBook = false })
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // हेडर
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Butt Network Identity", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF10B981).copy(alpha = 0.15f)) {
                        Text(
                            text = "Account Synced", color = Color(0xFF10B981), fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // अवतार और यूजरनेम
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 12.dp)) {
                    Box(modifier = Modifier.size(90.dp).clip(CircleShape).background(Color(0xFF334155)), contentAlignment = Alignment.Center) {
                        Text(text = state.username.take(2).uppercase(), color = Color(0xFF38BDF8), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isEditingUsername) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = usernameInput, onValueChange = { usernameInput = it }, modifier = Modifier.width(160.dp))
                            Button(onClick = { if (profileEngine.updateUsername(usernameInput)) isEditingUsername = false }) { Text("Save") }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isEditingUsername = true }) {
                            Text(text = "@${state.username}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "✏️", fontSize = 14.sp)
                        }
                    }
                }
            }

            // [न्यू एडन कार्ड] क्विक कांटेक्ट डायरेक्टरी बटन कार्ड
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth().clickable { showContactBook = true }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Address Book Directory", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Manage and save trusted payout addresses", color = Color(0xFF64748B), fontSize = 12.sp)
                        }
                        Text("📖 ➔", color = Color(0xFF0EA5E9), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // पेमेंट आईडी हब
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Payment Routing Hub", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Network UID:", color = Color(0xFF64748B))
                            Text(state.uid, color = Color.White, fontWeight = FontWeight.Monospace)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFF020617), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Text(text = "QR CODE SCANNER READY", color = Color(0xFF334155), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // सीक्रेट की वॉल्ट
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Backup Cryptographic Keys", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = if (state.isPrivateKeyVisible) "👁️ Hide" else "👁️‍🗨️ Unhide", color = Color(0xFF38BDF8), fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, modifier = Modifier.clickable { profileEngine.togglePrivateKeyVisibility() }
                            )
                        }
                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF020617), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Text(text = if (state.isPrivateKeyVisible) profileEngine.getSecretPrivateKey() else "••••••••••••••••••••••••••••••••", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Monospace)
                        }
                    }
                }
            }
        }
    }
}

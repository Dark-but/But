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
import com.clean.cryptowallet.data.settings.AppSettingsEngine
import com.clean.cryptowallet.data.settings.AppThemeMode
import com.clean.cryptowallet.data.settings.SupportedCurrency
import com.clean.cryptowallet.ui.contact.ContactBookScreen

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val secureStorage = remember { SecureStorageManager(context) }
    val profileEngine = remember { UserProfileEngine(secureStorage) }
    val settingsEngine = remember { AppSettingsEngine() }

    val state by profileEngine.profileState.collectAsState()
    val settingsState by settingsEngine.settingsState.collectAsState()

    var usernameInput by remember { mutableStateOf(state.username) }
    var isEditingUsername by remember { mutableStateOf(false) }
    var showContactBook by remember { mutableStateOf(false) }

    // डमी माइनिंग बैलेंस कैलकुलेटर के प्रदर्शन के लिए
    val dummySvcBalance = 1250.0

    if (showContactBook) {
        ContactBookScreen(onBackClicked = { showContactBook = false })
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(if (settingsState.themeMode == AppThemeMode.LIGHT) Color(0xFFF1F5F9) else Color(0xFF0F172A))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. हेडर और सिंक स्टेटस
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Sovereign Identity", color = if (settingsState.themeMode == AppThemeMode.LIGHT) Color.Black else Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF10B981).copy(alpha = 0.15f)) {
                        Text("Secure Node Active", color = Color(0xFF10B981), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }

            // 2. अवतार और यूजरनेम एडिटर
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF334155)), contentAlignment = Alignment.Center) {
                        Text(state.username.take(2).uppercase(), color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditingUsername) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = usernameInput, onValueChange = { usernameInput = it }, modifier = Modifier.width(150.dp))
                            Button(onClick = { if (profileEngine.updateUsername(usernameInput)) isEditingUsername = false }) { Text("Save") }
                        }
                    } else {
                        Text(
                            text = "@${state.username} ✏️",
                            color = if (settingsState.themeMode == AppThemeMode.LIGHT) Color.DarkGray else Color.White,
                            modifier = Modifier.clickable { isEditingUsername = true },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 3. लाइव ऑटो-करेंसी कनवर्टर कार्ड (कैलकुलेटर खत्म भाई!)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Estimated Portfolio Valuation", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Balance: $dummySvcBalance SVC", color = Color.White, fontWeight = FontWeight.Bold)
                            // लाइव कैलकुलेटेड रेट बिना किसी झंझट के
                            Text(
                                text = settingsEngine.calculateLocalBalance(dummySvcBalance, settingsState.selectedCurrency),
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // 4. [एड्रेस प्राइवेसी स्टार फीचर] और पेमेंट हब
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Mask Wallet Address (Privacy)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Switch(
                                checked = settingsState.isAddressMasked,
                                onCheckedChange = { settingsEngine.toggleAddressMasking() }
                            )
                        }
                        
                        // लाइव स्टार-स्टार या फुल एड्रेस डिस्प्ले
                        val displayedAddress = settingsEngine.maskWalletAddress(state.walletAddress, settingsState.isAddressMasked)
                        OutlinedTextField(
                            value = displayedAddress,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Your Cryptographic Destination") },
                            colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color(0xFF38BDF8)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 5. थीम और मल्टी-करेंसी स्विचर सेटिंग्स पैनल
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Global Preference Engine", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        // थीम रो (System / Light / Dark)
                        Text("Display Theme Mode", color = Color.White, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AppThemeMode.values().forEach { mode ->
                                Button(
                                    onClick = { settingsEngine.setTheme(mode) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (settingsState.themeMode == mode) Color(0xFF0EA5E9) else Color(0xFF020617)),
                                    modifier = Modifier.weight(1f)
                                ) { Text(mode.name, fontSize = 10.sp) }
                            }
                        }

                        // करेंसी सिलेक्शन ग्रिड रो
                        Text("Active Fiat Settlement Currency", color = Color.White, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(SupportedCurrency.USD, SupportedCurrency.INR, SupportedCurrency.EUR, SupportedCurrency.AED).forEach { curr ->
                                Button(
                                    onClick = { settingsEngine.setCurrency(curr) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (settingsState.selectedCurrency == curr) Color(0xFF10B981) else Color(0xFF020617)),
                                    modifier = Modifier.weight(1f)
                                ) { Text(curr.name, fontSize = 10.sp) }
                            }
                        }
                    }
                }
            }

            // 6. प्राइवेसी पॉलिसी, लाइसेंस और फीचर्स डायरेक्टरी
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Legal Documents & Infrastructure", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        
                        Text("📄 Privacy Policy: Decentralized localized storage. No data leaves the node.", color = Color.White, fontSize = 11.sp)
                        Text("⚖️ MIT Open-Source License: Fully decentralized, white-labeled cryptographic core system architecture.", color = Color.White, fontSize = 11.sp)
                        Text("🚀 Enabled Ecosystem Features: Cloud P2P Mining, ButtPay Engine, Core Instant Asset Swapper, Universal Transaction Ledger Audit Book.", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// कंपोज़ेबल क्लिकेबल एम्बिग्यूइटी प्रिवेंशन हेल्पर
private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(Modifier.background(Color.Transparent).shortClick(onClick))
@Composable private fun Modifier.shortClick(onClick: () -> Unit) = androidx.compose.foundation.clickable(onClick = onClick)

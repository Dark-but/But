package com.clean.cryptowallet.ui.contact

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
import com.clean.cryptowallet.data.contact.ContactEngine

@Composable
fun ContactBookScreen(onBackClicked: () -> Unit) {
    val contactEngine = remember { ContactEngine() }
    val contactsList by contactEngine.contacts.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        // हेडर रो
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sovereign Beneficiaries", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onBackClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
            ) {
                Text("Back", color = Color.White)
            }
        }

        // नया कांटेक्ट जोड़ने का फॉर्म कार्ड
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add New Address / UID", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Contact Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    label = { Text("Wallet Address or UID") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (contactEngine.addNewContact(nameInput, addressInput)) {
                            nameInput = ""
                            addressInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Save Contact Securely", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Saved Directory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // सेव्ड कांटेक्ट की लिस्ट व्यू
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (contactsList.isEmpty()) {
                item {
                    Text("No contacts saved yet.", color = Color(0xFF64748B), fontSize = 14.sp)
                }
            } else {
                items(contactsList) { contact ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(contact.addressOrUid, color = Color(0xFF94A3B8), fontSize = 11.sp, maxLines = 1)
                            }
                            
                            // डिलीट ऐक्शन बटन
                            Text(
                                text = "🗑️",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .padding(6.dp)
                                    .maxHeight(30.dp)
                                    .align(Alignment.CenterVertically)
                                    .contentDescription("Delete")
                                    .clickable { contactEngine.deleteContact(contact.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// हेल्पर एक्सटेंशन ताकि एरर न आये
private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(Modifier.background(Color.Transparent).shortClick(onClick))
@Composable private fun Modifier.shortClick(onClick: () -> Unit) = androidx.compose.foundation.clickable(onClick = onClick)

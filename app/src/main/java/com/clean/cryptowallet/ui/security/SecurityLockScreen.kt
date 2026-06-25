package com.clean.cryptowallet.ui.security

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun SecurityLockScreen(viewModel: SecurityViewModel, onUnlockSuccess: () -> Unit) {
    val state by viewModel.lockState.collectAsState()

    // यदि ऐप अनलॉक हो चुका है, तो मुख्य स्क्रीन पर भेजें
    if (!state.isAppLacked) {
        LaunchedEffect(Unit) {
            onUnlockSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // थीम के अनुसार डार्क बैकग्राउंड
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. हेडर टेक्स्ट (स्थिति के अनुसार बदलता हुआ)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 60.dp)
            ) {
                Text(
                    text = if (!state.isPinSetupComplete) "Setup Security PIN" else "Enter Code Securely",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (!state.isPinSetupComplete) "Create a 4-digit PIN to lock your wallet" else "Confirm identity to access wallet",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, horizontal = 20.dp)
                )
            }

            // 2. पिन इंडिकेटर डॉट्स (••••)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 40.dp)
            ) {
                for (i in 1..4) {
                    val isFilled = state.currentInputPin.length >= i
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    state.wrongPinEntered -> Color(0xFFEF4444) // गलत पिन पर लाल डॉट्स
                                    isFilled -> Color(0xFF0EA5E9) // भरे हुए डॉट्स (ब्लू)
                                    else -> Color(0xFF334155) // खाली डॉट्स
                                }
                            )
                    )
                }
            }

            // गलत पिन का एरर मैसेज
            if (state.wrongPinEntered) {
                Text(
                    text = "Incorrect PIN. Attempts left: ${state.remainingAttempts}",
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. कस्टम न्यूमेरिक कीपैड (0-9 Grid)
            Column(
                modifier = Modifier.padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "backspace")
                )

                keys.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.2f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (key.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B))
                                            .clickable {
                                                if (key == "backspace") {
                                                    viewModel.onBackspacePressed()
                                                } else {
                                                    viewModel.onNumberPressed(key)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (key == "backspace") {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowBack,
                                                contentDescription = "Delete",
                                                tint = Color.White
                                            )
                                        } else {
                                            Text(
                                                text = key,
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

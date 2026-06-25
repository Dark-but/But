package com.clean.cryptowallet.ui.security

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.security.PinSecurityEngine

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun AppLockScreen(pinEngine: PinSecurityEngine, onUnlockSuccess: () -> Unit) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // हेडर सेक्शन
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Text(
                text = "Butt Network Secure Vault",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isError) "Incorrect PIN! Try Again." else "Enter 4-Digit Security PIN to Unlock",
                color = if (isError) Color(0xFFEF4444) else Color(0xFF64748B),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // पिन डॉट्स इंडिकेटर (4 डॉट्स)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 32.dp)
            ) {
                for (i in 1..4) {
                    val isFilled = pinInput.length >= i
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) Color(0xFF0EA5E9) else Color(0xFF334155))
                    )
                }
            }
        }

        // 3x4 डिजिटल पिन-पैड ग्रिड
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            val numPad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("Clear", "0", "🔓")
            )

            numPad.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    alignment = Alignment.CenterVertically
                ) {
                    row.forEach { digit ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(65.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    isError = false
                                    when (digit) {
                                        "Clear" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                        "🔓" -> {
                                            if (pinEngine.verifyPin(pinInput)) {
                                                onUnlockSuccess()
                                            } else {
                                                pinInput = ""
                                                isError = true
                                            }
                                        }
                                        else -> {
                                            if (pinInput.length < 4) {
                                                pinInput += digit
                                                // अगर 4 डिजिट पूरे हो गए तो ऑटो-चेक करें
                                                if (pinInput.length == 4) {
                                                    if (pinEngine.verifyPin(pinInput)) {
                                                        onUnlockSuccess()
                                                    } else {
                                                        pinInput = ""
                                                        isError = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                color = if (digit == "🔓" || digit == "Clear") Color(0xFF38BDF8) else Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

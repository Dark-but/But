package com.clean.cryptowallet.ui.onboarding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clean.cryptowallet.data.security.SecureStorageManager
import com.clean.cryptowallet.data.security.PinSecurityEngine
import com.clean.cryptowallet.ui.dashboard.DashboardContainer
import com.clean.cryptowallet.ui.security.AppLockScreen

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val secureStorage = remember { SecureStorageManager(context) }
    val pinEngine = remember { PinSecurityEngine(secureStorage) }
    
    // अगर वॉलेट पुराना है, तो शुरुआत में ऐप लॉक्ड रहेगा (isAppUnlocked = false)
    var isAppUnlocked by remember { mutableStateOf(!state.isWalletActive) }

    if (state.isWalletActive) {
        if (isAppUnlocked) {
            DashboardContainer()
        } else {
            AppLockScreen(pinEngine = pinEngine, onUnlockSuccess = {
                isAppUnlocked = true
            })
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state.currentStep) {
                OnboardingStep.WELCOME -> WelcomeLayout(viewModel)
                OnboardingStep.CREATE_DISPLAY -> CreateWalletLayout(state.generatedMnemonic, viewModel)
                OnboardingStep.IMPORT_INPUT -> ImportWalletLayout(state.importInputText, state.errorMessage, viewModel)
                OnboardingStep.SUCCESS -> { /* हैंडलड ऊपर */ }
            }
        }
    }
}

@Composable
fun WelcomeLayout(viewModel: OnboardingViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "But Network",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Secure • Anonymous • Utility Suite",
            color = Color(0xFF38BDF8),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
        )

        Button(
            onClick = { viewModel.navigateToCreate() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            Text("Create New 24-Word Wallet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.navigateToImport() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            Text("Import Existing Seed Phrase", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CreateWalletLayout(mnemonic: List<String>, viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Your 24-Word Recovery Phrase", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Write down these words in correct order. This is the ONLY way to recover your assets.",
            color = Color(0xFFEF4444),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(mnemonic) { index, word ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${index + 1}. $word",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Monospace
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.backToWelcome() },
                modifier = Modifier.weight(1f)
            ) { Text("Back") }

            Button(
                onClick = { viewModel.confirmCreateWallet() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.weight(1.5f)
            ) { Text("I've Saved It", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun ImportWalletLayout(inputText: String, error: String?, viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Import Butt Network Wallet", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Paste or type your 24-word recovery seed phrase below.", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { viewModel.onImportTextChange(it) },
            label = { Text("Enter 24 Words") },
            placeholder = { Text("alpha bravo charlie...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0EA5E9),
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp),
            maxLines = 10
        )

        if (error != null) {
            Text(text = error, color = Color(0xFFEF4444), fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.backToWelcome() },
                modifier = Modifier.weight(1f)
            ) { Text("Back") }

            Button(
                onClick = { viewModel.submitImportWallet() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                modifier = Modifier.weight(1.5f)
            ) { Text("Import Wallet", fontWeight = FontWeight.Bold) }
        }
    }
}

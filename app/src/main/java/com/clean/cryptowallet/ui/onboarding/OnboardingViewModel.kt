package com.clean.cryptowallet.ui.onboarding

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.clean.cryptowallet.data.security.SecureStorageManager
import com.clean.cryptowallet.data.onboarding.OnboardingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class OnboardingStep {
    WELCOME, CREATE_DISPLAY, IMPORT_INPUT, SUCCESS
}

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val generatedMnemonic: List<String> = emptyList(),
    val importInputText: String = "",
    val errorMessage: String? = null,
    val isWalletActive: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.M)
class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val secureStorage = SecureStorageManager(application.applicationContext)
    private val engine = OnboardingEngine(secureStorage)

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        if (engine.isWalletExisting()) {
            _uiState.update { it.copy(currentStep = OnboardingStep.SUCCESS, isWalletActive = true) }
        }
    }

    fun navigateToCreate() {
        val mnemonic = engine.generateNewMnemonic()
        _uiState.update { it.copy(currentStep = OnboardingStep.CREATE_DISPLAY, generatedMnemonic = mnemonic, errorMessage = null) }
    }

    fun navigateToImport() {
        _uiState.update { it.copy(currentStep = OnboardingStep.IMPORT_INPUT, errorMessage = null) }
    }

    fun backToWelcome() {
        _uiState.update { it.copy(currentStep = OnboardingStep.WELCOME, errorMessage = null) }
    }

    fun confirmCreateWallet() {
        engine.finalizeWalletCreation(_uiState.value.generatedMnemonic)
        _uiState.update { it.copy(currentStep = OnboardingStep.SUCCESS, isWalletActive = true) }
    }

    fun onImportTextChange(text: String) {
        _uiState.update { it.copy(importInputText = text) }
    }

    fun submitImportWallet() {
        val success = engine.validateAndImportWallet(_uiState.value.importInputText)
        if (success) {
            _uiState.update { it.copy(currentStep = OnboardingStep.SUCCESS, isWalletActive = true) }
        } else {
            _uiState.update { it.copy(errorMessage = "Invalid Phrase! Must be exactly 24 words separated by spaces.") }
        }
    }
}

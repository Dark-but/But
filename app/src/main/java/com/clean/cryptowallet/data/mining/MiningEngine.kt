package com.clean.cryptowallet.data.mining

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * यह हमारे खुद के कॉइन का कोर माइनिंग इंजन है।
 * यह बैकग्राउंड थ्रेड पर कोटलिन फ्लो (Flow) और कोरूटिन का इस्तेमाल करके माइनिंग कैलकुलेट करता है।
 */
@RequiresApi(Build.VERSION_CODES.M)
class MiningEngine(private val secureStorage: SecureStorageManager) {

    private val _miningState = MutableStateFlow(MiningState())
    val miningState: StateFlow<MiningState> = _miningState.asStateFlow()

    private var miningJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        // स्टोरेज से पहले से माइन किए गए कॉइन्स का बैलेंस लोड करना
        val savedBalance = secureStorage.getMiningBalance()
        _miningState.update { it.copy(totalCoinsMined = savedBalance) }
    }

    /**
     * माइनिंग की प्रक्रिया को सुरक्षित तरीके से शुरू करना।
     */
    fun startMining() {
        if (_miningState.value.isMiningActive) return

        _miningState.update { it.copy(isMiningActive = true, lastMiningTimestamp = System.currentTimeMillis()) }

        // बैकग्राउंड में हर 1 सेकंड पर कॉइन जनरेट करने का लूप
        miningJob = scope.launch {
            while (true) {
                delay(1000L) // 1 सेकंड का डिले
                _miningState.update { currentState ->
                    val currentCoins = currentState.totalCoinsMined
                    val speed = currentState.hashrateSpeed
                    val newBalance = currentCoins + speed

                    // हर सेकंड होने वाली माइनिंग को लोकल डिवाइस के सुरक्षित स्टोरेज में सेव करना
                    secureStorage.saveMiningBalance(newBalance)

                    currentState.copy(
                        totalCoinsMined = newBalance,
                        nextRewardBlockTime = if (currentState.nextRewardBlockTime <= 1) 600L else currentState.nextRewardBlockTime - 1
                    )
                }
            }
        }
    }

    /**
     * माइनिंग को रोकना (Stop) और अंतिम बैलेंस को सुरक्षित सेव करना।
     */
    fun stopMining() {
        miningJob?.cancel()
        miningJob = null
        _miningState.update { it.copy(isMiningActive = false) }
        secureStorage.saveMiningBalance(_miningState.value.totalCoinsMined)
    }

    /**
     * माइनिंग की स्पीड (Hashrate) को बूस्ट या अपग्रेड करना (जैसे माइनिंग स्टेप्स पूरे होने पर)।
     */
    fun boostHashrate(additionalSpeed: Double) {
        _miningState.update { currentState ->
            currentState.copy(hashrateSpeed = currentState.hashrateSpeed + additionalSpeed)
        }
    }
}

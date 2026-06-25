package com.clean.cryptowallet.data.admin

import android.os.Build
import androidx.annotation.RequiresApi
import com.clean.cryptowallet.data.security.SecureStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(Build.VERSION_CODES.M)
class AdminRewardEngine(private val secureStorage: SecureStorageManager) {

    private val _rewardState = MutableStateFlow(RewardState())
    val rewardState: StateFlow<RewardState> = _rewardState.asStateFlow()

    /**
     * बट पे (ButtPay) के लिए एडमिन फीस कैलकुलेटर।
     * यह फंक्शन ट्रांसफर अमाउंट में से 1% फीस काटकर एडमिन के खाते का लॉजिक सेट करता है।
     */
    fun calculateAndDeductAdminFee(amount: Double): Double {
        val feeRate = _rewardState.value.currentAdminFeeRate
        val adminFee = amount * feeRate
        
        // बची हुई रकम जो यूजर को डिलीवर होगी
        return adminFee
    }

    /**
     * यूजर का डेली रिवॉर्ड (Daily Login Reward) क्लेम करने का लॉजिक।
     */
    fun claimDailyReward(): Boolean {
        if (_rewardState.value.dailyRewardClaimed) return false

        val currentBalance = secureStorage.getMiningBalance()
        val reward = _rewardState.value.dailyRewardAmount
        val newBalance = currentBalance + reward

        // सुरक्षित तरीके से बैलेंस को अपडेट करना
        secureStorage.saveMiningBalance(newBalance)

        _rewardState.update { currentState ->
            currentState.copy(
                dailyRewardClaimed = true,
                totalRewardsEarned = currentState.totalRewardsEarned + reward
            )
        }
        return true
    }

    /**
     * टास्क या स्टेप्स पूरे करने पर स्पेशल रिवॉर्ड क्रेडिट करना।
     */
    fun creditTaskReward() {
        val currentBalance = secureStorage.getMiningBalance()
        val taskReward = _rewardState.value.watchAdRewardAmount
        val newBalance = currentBalance + taskReward

        secureStorage.saveMiningBalance(newBalance)

        _rewardState.update { currentState ->
            currentState.copy(
                totalRewardsEarned = currentState.totalRewardsEarned + taskReward
            )
        }
    }
}

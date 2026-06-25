package com.clean.cryptowallet.ui.mining

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.clean.cryptowallet.data.mining.MiningEngine
import com.clean.cryptowallet.data.mining.MiningState
import com.clean.cryptowallet.data.security.SecureStorageManager
import kotlinx.coroutines.flow.StateFlow

/**
 * माइनिंग स्क्रीन के बिजनेस लॉजिक को संभालने वाली मुख्य क्लास।
 * यह एंड्रॉइड लाइफसाइकिल के अनुसार माइनिंग स्टेट को मैनेज करती है।
 */
@RequiresApi(Build.VERSION_CODES.M)
class MiningViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorageManager(application.applicationContext)
    private val miningEngine = MiningEngine(secureStorage)

    // स्क्रीन इस स्टेट को ऑब्जर्व (Live Listen) करेगी
    val miningState: StateFlow<MiningState> = miningEngine.miningState

    fun toggleMining() {
        if (miningState.value.isMiningActive) {
            miningEngine.stopMining()
        } else {
            miningEngine.startMining()
        }
    }

    fun applyStepBoost() {
        // यूजर द्वारा माइनिंग स्टेप्स पूरे करने पर स्पीड बढ़ाना (+0.0005 कॉइन्स/सेकंड)
        miningEngine.boostHashrate(0.0005)
    }

    override fun onCleared() {
        super.onCleared()
        // जब ऐप बंद हो, तो माइनिंग को सुरक्षित रोक देना ताकि मेमोरी लीक न हो
        miningEngine.stopMining()
    }
}

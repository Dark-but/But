package com.clean.cryptowallet.ui.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clean.cryptowallet.data.network.NetworkSyncEngine
import com.clean.cryptowallet.data.network.NetworkUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * नेटवर्क सिंकिंग और सर्वर कनेक्शन स्टेट को मैनेज करने वाला व्यू-मॉडल।
 */
class NetworkViewModel : ViewModel() {

    private val networkSyncEngine = NetworkSyncEngine()

    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    init {
        // ऐप चालू होते ही सर्वर से सिंक करना शुरू करें
        syncWithLiveBlockchainNode()
    }

    /**
     * सर्वर से डेटा दोबारा मैन्युअल रीफ्रेश या ऑटो-सिंक करने के लिए।
     */
    fun syncWithLiveBlockchainNode() {
        _uiState.update { it.copy(isSyncing = true, apiErrorMessage = null) }

        viewModelScope.launch {
            try {
                val freshStatus = networkSyncEngine.fetchLiveNetworkStatus()
                _uiState.update { currentState ->
                    currentState.copy(
                        isSyncing = false,
                        nodeStatus = freshStatus,
                        isConnectionSecure = freshStatus.isServerSynced
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSyncing = false, 
                        apiErrorMessage = "Node connection timeout. Retrying..."
                    ) 
                }
            }
        }
    }
}

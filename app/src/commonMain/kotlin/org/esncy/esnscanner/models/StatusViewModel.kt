package org.esncy.esnscanner.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.esncy.esnscanner.data.APIService
import org.esncy.esnscanner.data.KtorClients
import org.esncy.esnscanner.ui.screens.CameraViewModel

sealed interface StatusUIState {
    data object Idle : StatusUIState
    data object Loading : StatusUIState
    data class Success(val result: StatusResult, val lastScan: String) : StatusUIState
    data class Error(val message: String, val lastScan: String) : StatusUIState
}

data class StatusResult(
    var cardNumber: String,
    var status: String
)

enum class Statuses {
    Paid,
    Issued,
    Delivered,
    Blacklisted
}

class StatusViewModel(
    private val dataFlow: StateFlow<SectionData>,
    val status: Statuses,
    private val clients: KtorClients
) : ViewModel(), CameraViewModel<StatusUIState> {
    val sectionData: SectionData
        get() = dataFlow.value

    private val apiService: APIService
        get() = APIService(
            publicClient = clients.publicClient,
            privateClient = clients.privateClient,
            sectionDomain = sectionData.localSectionDomain,
            spreadsheetID = sectionData.spreadsheetID,
        )

    private val _state = MutableStateFlow<StatusUIState>(StatusUIState.Idle)
    override val state = _state.asStateFlow()

    override fun onScan(scannedString: String) {
        if (_state.value is StatusUIState.Loading) return

        val lastScan =
            if (_state.value is StatusUIState.Success) (_state.value as StatusUIState.Success).lastScan else if (_state.value is StatusUIState.Error) (_state.value as StatusUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _state.value = StatusUIState.Loading

        var identifier: String
        val esncardMatch = ESNCardNumberRegex.find(scannedString)
        if (esncardMatch != null)
            identifier = esncardMatch.value
        else {
            val freePassMatch = FreePassRegex.find(scannedString)
            if (freePassMatch == null) {
                _state.value = StatusUIState.Error("Invalid", scannedString)
                return
            }
            identifier = freePassMatch.value
        }

        viewModelScope.launch {
            val deliverResponse = apiService.updateStatus(identifier, status.name)
            if (deliverResponse == null) {
                _state.value = StatusUIState.Error("Unknown Error", scannedString)
                return@launch
            }
            if (deliverResponse.status == "error") {
                _state.value = StatusUIState.Error(deliverResponse.message, scannedString)
                return@launch
            }
            _state.value = StatusUIState.Success(
                StatusResult(
                    identifier,
                    "Success"
                ), scannedString
            )
        }
    }
}
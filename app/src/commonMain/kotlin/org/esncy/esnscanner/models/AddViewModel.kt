package org.esncy.esnscanner.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.perf.performance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.esncy.esnscanner.data.ApiServiceImplementation
import org.esncy.esnscanner.data.KtorClient
import org.esncy.esnscanner.ui.screens.CameraViewModel

sealed interface AddUIState {
    data object Idle : AddUIState
    data object Loading : AddUIState
    data class Success(val result: AddResult, val lastScan: String) : AddUIState
    data class Error(val message: String, val lastScan: String) : AddUIState
}

data class AddResult(
    var cardNumber: String,
    var status: String
)

class AddViewModel(
    private val dataFlow: StateFlow<SectionData>
) : ViewModel(), CameraViewModel<AddUIState> {
    val sectionData: SectionData
        get() = dataFlow.value

    private val apiService: ApiServiceImplementation
        get() = ApiServiceImplementation(
            client = KtorClient.httpClient,
            sectionDomain = sectionData.localSectionDomain,
            spreadsheetID = sectionData.spreadsheetID,
        )

    private val _state = MutableStateFlow<AddUIState>(AddUIState.Idle)
    override val state = _state.asStateFlow()

    override fun onScan(scannedString: String) {
        if (_state.value is AddUIState.Loading) return

        val trace = Firebase.performance.newTrace("scan_card_duration")
        trace.start()

        val lastScan =
            if (_state.value is AddUIState.Success) (_state.value as AddUIState.Success).lastScan else if (_state.value is AddUIState.Error) (_state.value as AddUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _state.value = AddUIState.Loading

        var card = scannedString
        val esncardMatch = ESNCardNumberRegex.find(card)
        val isESNcard = esncardMatch != null
        if (isESNcard)
            card = esncardMatch.value
        else {
            _state.value = AddUIState.Error("Invalid", scannedString)
            return
        }

        viewModelScope.launch {
            val addResponse = apiService.addCard(card)
            if (addResponse == null) {
                _state.value = AddUIState.Error("Unknown Error", scannedString)
                return@launch
            }
            if (addResponse.status == "error") {
                _state.value = AddUIState.Error(addResponse.message, scannedString)
                return@launch
            }
            _state.value = AddUIState.Success(
                AddResult(
                    card,
                    "Success"
                ), scannedString
            )
            trace.stop()
        }
    }
}
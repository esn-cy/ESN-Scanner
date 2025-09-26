package com.andymic.esnscanner.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.ApiServiceImplementation
import com.andymic.esnscanner.data.KtorClient
import com.andymic.esnscanner.data.SectionData
import com.andymic.esnscanner.data.Sections
import com.andymic.esnscanner.ui.components.CameraViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DeliverUIState {
    data object Idle : DeliverUIState
    data object Loading : DeliverUIState
    data class Success(val result: DeliverResult, val lastScan: String) : DeliverUIState
    data class Error(val message: String, val lastScan: String) : DeliverUIState
}

data class DeliverResult(
    var cardNumber: String,
    var status: String
)

class DeliverViewModel(application: Application) : AndroidViewModel(application),
    CameraViewModel<DeliverUIState> {
    val sections: List<Sections.Section>
    val sectionData: SectionData.SectionData

    init {
        val sectionsInputStream = application.assets.open("sections.json")
        sections = Sections(sectionsInputStream).sections
        val sectionDataInputStream = application.assets.open("section-data.json")
        sectionData = SectionData(sectionDataInputStream).sectionData
    }

    private val apiService = ApiServiceImplementation(KtorClient.httpClient)

    private val _state = MutableStateFlow<DeliverUIState>(DeliverUIState.Idle)
    override val state = _state.asStateFlow()

    override fun onScan(scannedString: String) {
        if (_state.value is DeliverUIState.Loading) return

        val lastScan =
            if (_state.value is DeliverUIState.Success) (_state.value as DeliverUIState.Success).lastScan else if (_state.value is DeliverUIState.Error) (_state.value as DeliverUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _state.value = DeliverUIState.Loading

        var card = scannedString
        val esncardMatch = ESNCardNumberRegex.find(card)
        val isESNcard = esncardMatch != null
        if (isESNcard)
            card = esncardMatch.value
        else {
            _state.value = DeliverUIState.Error("Invalid", scannedString)
            return
        }

        viewModelScope.launch {
            val deliverResponse = apiService.updateStatus(card, "Delivered")
            if (deliverResponse == null) {
                _state.value = DeliverUIState.Error("Unknown Error", scannedString)
                return@launch
            }
            if (deliverResponse.status == "error") {
                _state.value = DeliverUIState.Error(deliverResponse.message, scannedString)
                return@launch
            }
            _state.value = DeliverUIState.Success(
                DeliverResult(
                    card,
                    "Success"
                ), scannedString
            )
        }
    }
}
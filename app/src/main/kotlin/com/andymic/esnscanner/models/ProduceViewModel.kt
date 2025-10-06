package com.andymic.esnscanner.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.ApiServiceImplementation
import com.andymic.esnscanner.data.KtorClient
import com.andymic.esnscanner.data.SectionData
import com.andymic.esnscanner.data.Sections
import com.andymic.esnscanner.ui.screens.CameraViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProduceUIState {
    data object Idle : ProduceUIState
    data object Loading : ProduceUIState
    data class Success(val result: ProduceResult, val lastScan: String) : ProduceUIState
    data class Error(val message: String, val lastScan: String) : ProduceUIState
}

data class ProduceResult(
    var cardNumber: String,
    var status: String
)

class ProduceViewModel(application: Application) : AndroidViewModel(application),
    CameraViewModel<ProduceUIState> {
    val sections: List<Sections.Section>
    val sectionData: SectionData.SectionData

    init {
        val sectionsInputStream = application.assets.open("sections.json")
        sections = Sections(sectionsInputStream).sections
        val sectionDataInputStream = application.assets.open("section-data.json")
        sectionData = SectionData(sectionDataInputStream).sectionData
    }

    private val apiService = ApiServiceImplementation(KtorClient.httpClient)

    private val _state = MutableStateFlow<ProduceUIState>(ProduceUIState.Idle)
    override val state = _state.asStateFlow()

    override fun onScan(scannedString: String) {
        if (_state.value is ProduceUIState.Loading) return

        val lastScan =
            if (_state.value is ProduceUIState.Success) (_state.value as ProduceUIState.Success).lastScan else if (_state.value is ProduceUIState.Error) (_state.value as ProduceUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _state.value = ProduceUIState.Loading

        var card = scannedString
        val esncardMatch = ESNCardNumberRegex.find(card)
        val isESNcard = esncardMatch != null
        if (isESNcard)
            card = esncardMatch.value
        else {
            _state.value = ProduceUIState.Error("Invalid", scannedString)
            return
        }

        viewModelScope.launch {
            val produceResponse = apiService.updateStatus(card, "Issued")
            if (produceResponse == null) {
                _state.value = ProduceUIState.Error("Unknown Error", scannedString)
                return@launch
            }
            if (produceResponse.status == "error") {
                _state.value = ProduceUIState.Error(produceResponse.message, scannedString)
                return@launch
            }
            _state.value = ProduceUIState.Success(
                ProduceResult(
                    card,
                    "Success"
                ), scannedString
            )
        }
    }
}
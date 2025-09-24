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

class AddViewModel(application: Application) : AndroidViewModel(application),
    CameraViewModel<AddUIState> {
    val sections: List<Sections.Section>
    val sectionData: SectionData.SectionData

    init {
        val sectionsInputStream = application.assets.open("sections.json")
        sections = Sections(sectionsInputStream).sections
        val sectionDataInputStream = application.assets.open("section-data.json")
        sectionData = SectionData(sectionDataInputStream).sectionData
    }

    private val apiService = ApiServiceImplementation(KtorClient.httpClient)

    private val _state = MutableStateFlow<AddUIState>(AddUIState.Idle)
    override val state = _state.asStateFlow()

    override fun onScan(scannedString: String) {
        if (_state.value is AddUIState.Loading) return

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
        }
    }
}
package com.andymic.esnscanner.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.KtorClient
import com.andymic.esnscanner.data.SectionData
import com.andymic.esnscanner.data.TestServiceImplementation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ConnectionUIState {
    data object Idle : ConnectionUIState
    data object Loading : ConnectionUIState
    data class Success(val result: ConnectionResult) : ConnectionUIState
}

data class ConnectionResult(
    var serviceStatus: String,
    var isLocalOnline: Boolean,
    var isInternationalOnline: Boolean,
    val isDatasetOnline: Boolean
)

class ConnectionViewModel(application: Application) : AndroidViewModel(application) {
    val sectionData: SectionData.SectionData

    init {
        val sectionDataInputStream = application.assets.open("section-data.json")
        sectionData = SectionData(sectionDataInputStream).sectionData
    }

    private val testService = TestServiceImplementation(KtorClient.httpClient)

    private val _state = MutableStateFlow<ConnectionUIState>(ConnectionUIState.Idle)
    val state = _state.asStateFlow()

    fun runTest() {
        if (_state.value is ConnectionUIState.Loading) return
        _state.value = ConnectionUIState.Loading

        viewModelScope.launch {
            val isLocalOnline = testService.local(sectionData.localSectionDomain)
            val isInternationalOnline = testService.international()
            val isDatasetOnline = testService.dataset(sectionData.spreadsheetID)

            val serviceStatus =
                if (isLocalOnline && isInternationalOnline && isDatasetOnline)
                    "Full Information Available"
                else if ((isLocalOnline || isDatasetOnline) && !isInternationalOnline)
                    "Local Information Only"
                else if (!(isLocalOnline || isDatasetOnline) && isInternationalOnline)
                    "International Information Only"
                else
                    "No Information Available"

            _state.value = ConnectionUIState.Success(
                ConnectionResult(
                    serviceStatus = serviceStatus,
                    isLocalOnline = isLocalOnline,
                    isInternationalOnline = isInternationalOnline,
                    isDatasetOnline = isDatasetOnline
                )
            )
        }
    }
}
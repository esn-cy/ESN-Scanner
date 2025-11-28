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


sealed interface OnlineUIState {
    data object Idle : OnlineUIState
    data object Loading : OnlineUIState
    data class Success(val result: OnlineResult) : OnlineUIState
}

data class OnlineResult(
    var serviceStatus: String,
    var isLocalOnline: Boolean,
    var isInternationalOnline: Boolean,
    val isDatasetOnline: Boolean
)

class OnlineViewModel(
    application: Application,
    private val sectionData: SectionData.SectionData
) : AndroidViewModel(application) {
    private val testService = TestServiceImplementation(
        client = KtorClient.httpClient,
        sectionDomain = sectionData.localSectionDomain,
        spreadsheetID = sectionData.spreadsheetID,
    )

    private val _state = MutableStateFlow<OnlineUIState>(OnlineUIState.Idle)
    val state = _state.asStateFlow()

    fun runTest() {
        if (_state.value is OnlineUIState.Loading) return
        _state.value = OnlineUIState.Loading

        viewModelScope.launch {
            val isLocalOnline: Boolean = if (sectionData.localSectionDomain != "")
                testService.local() else false
            val isInternationalOnline: Boolean = testService.international()
            val isDatasetOnline: Boolean = if (sectionData.spreadsheetID != "")
                testService.dataset() else false

            val serviceStatus =
                if (isLocalOnline && isInternationalOnline && isDatasetOnline)
                    "Full Information Available"
                else if ((isLocalOnline || isDatasetOnline) && !isInternationalOnline)
                    "Local Information Only"
                else if (!(isLocalOnline || isDatasetOnline) && isInternationalOnline)
                    "International Information Only"
                else
                    "No Information Available"

            _state.value = OnlineUIState.Success(
                OnlineResult(
                    serviceStatus = serviceStatus,
                    isLocalOnline = isLocalOnline,
                    isInternationalOnline = isInternationalOnline,
                    isDatasetOnline = isDatasetOnline
                )
            )
        }
    }
}
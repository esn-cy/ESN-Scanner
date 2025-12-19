package com.andymic.esnscanner.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.KtorClient
import com.andymic.esnscanner.data.TestServiceImplementation
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.perf.performance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Clock

sealed interface OnlineUIState {
    data object Idle : OnlineUIState
    data object Loading : OnlineUIState
    data class Success(val result: OnlineResult) : OnlineUIState
}

data class OnlineResult(
    val timestamp: Long,
    var serviceStatus: String,
    var isLocalOnline: Boolean,
    var isInternationalOnline: Boolean,
    val isDatasetOnline: Boolean
)

class OnlineViewModel(
    private val dataFlow: StateFlow<SectionData>
) : ViewModel() {
    val sectionData: SectionData
        get() = dataFlow.value

    private val testService: TestServiceImplementation
        get() = TestServiceImplementation(
            client = KtorClient.httpClient,
            sectionDomain = sectionData.localSectionDomain,
            spreadsheetID = sectionData.spreadsheetID,
        )

    private val _state = MutableStateFlow<OnlineUIState>(OnlineUIState.Idle)
    val state = _state.asStateFlow()

    private var lastTestedData: SectionData? = null

    init {
        viewModelScope.launch {
            dataFlow.collectLatest { data ->
                val isDataChanged = data != lastTestedData

                if (!isDataChanged && isCacheValid()) {
                    return@collectLatest
                }

                lastTestedData = data
                performTest(data)
            }
        }
    }

    private fun isCacheValid(): Boolean {
        val currentState = _state.value

        if (currentState is OnlineUIState.Success) {
            val successResult = (_state.value as OnlineUIState.Success).result
            val localTime = Clock.System.now().epochSeconds

            val isFresh = localTime - successResult.timestamp < 1800
            val isFull =
                successResult.isLocalOnline &&
                        successResult.isInternationalOnline &&
                        successResult.isDatasetOnline

            return isFresh && isFull
        }
        return false
    }

    fun runTest() {
        if (_state.value is OnlineUIState.Loading) return

        viewModelScope.launch {
            performTest(sectionData)
        }
    }

    private suspend fun performTest(sectionData: SectionData) {
        _state.value = OnlineUIState.Loading

        val trace = Firebase.performance.newTrace("online_test_duration")

        trace.start()

        try {
            val isLocalOnline: Boolean = if (sectionData.localSectionDomain != "")
                testService.local() else false
            val isInternationalOnline: Boolean = testService.international()
            val isDatasetOnline: Boolean = if (sectionData.spreadsheetID != "")
                testService.dataset() else false

            val serviceStatus =
                when (Triple(isLocalOnline, isInternationalOnline, isDatasetOnline)) {
                    Triple(true, true, true) -> "Full Information Available"
                    Triple(true, false, true) -> "Section Information Only"
                    Triple(true, false, false) -> "Website Information Only"
                    Triple(false, false, true) -> "Dataset Information Only"
                    Triple(false, true, false) -> "International Information Only"
                    Triple(true, true, false) -> "Partial Information"
                    Triple(false, true, true) -> "Partial Information"
                    Triple(false, false, false) -> "No Information Available"
                    else -> "Unknown Information Availability"
                }

            _state.value = OnlineUIState.Success(
                OnlineResult(
                    timestamp = Clock.System.now().epochSeconds,
                    serviceStatus = serviceStatus,
                    isLocalOnline = isLocalOnline,
                    isInternationalOnline = isInternationalOnline,
                    isDatasetOnline = isDatasetOnline
                )
            )
            trace.putMetric("was_cancelled", 0)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                trace.putMetric("was_cancelled", 1)
                throw e
            }
            _state.value = OnlineUIState.Idle
        } finally {
            trace.stop()
        }
    }
}
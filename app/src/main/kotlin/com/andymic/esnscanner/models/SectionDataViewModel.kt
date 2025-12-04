package com.andymic.esnscanner.models

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.SectionData
import com.andymic.esnscanner.dataStore
import com.andymic.esnscanner.ui.activities.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SectionDataUIState {
    data object Idle : SectionDataUIState
    data object Loading : SectionDataUIState
    data class Success(val result: SectionData.SectionData) : SectionDataUIState
    data class Error(val error: Exception? = null) : SectionDataUIState
}

class SectionDataViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<SectionDataUIState>(SectionDataUIState.Idle)
    val state = _state.asStateFlow()

    private val dataFlow = application.dataStore.data

    init {
        getData()
    }

    fun getData() {
        if (_state.value is SectionDataUIState.Loading)
            return
        try {
            viewModelScope.launch {
                _state.value = SectionDataUIState.Loading
                val data = dataFlow.first()
                _state.value = SectionDataUIState.Success(data)
            }
        } catch (e: Exception) {
            _state.value = SectionDataUIState.Error(e)
        }
    }

    fun updateData(
        localSectionName: String? = null,
        localSectionCode: String? = null,
        localSectionDomain: String? = null,
        spreadsheetID: String? = null
    ) {
        try {
            viewModelScope.launch {
                val value = application.dataStore.updateData { data ->
                    data.copy(
                        localSectionName = localSectionName ?: data.localSectionName,
                        localSectionCode = localSectionCode ?: data.localSectionCode,
                        localSectionDomain = localSectionDomain ?: data.localSectionDomain,
                        spreadsheetID = spreadsheetID ?: data.spreadsheetID
                    )
                }
                _state.value = SectionDataUIState.Success(value)
                val intent = Intent(application, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(intent)
                Runtime.getRuntime().exit(0)
            }
        } catch (e: Exception) {
            _state.value = SectionDataUIState.Error(e)
        }
    }
}
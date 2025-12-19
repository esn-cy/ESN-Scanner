package com.andymic.esnscanner.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UpdateUIState {
    data object Idle : UpdateUIState
    data object Loading : UpdateUIState
    data class Success(val result: UpdateResult) : UpdateUIState
    data class Error(val message: String) : UpdateUIState
}

data class UpdateResult(
    var isUpdateAvailable: Boolean,
    var updateInfo: Any? = null
)

expect class UpdateChecker() {
    suspend fun checkForUpdate(context: Any?): UpdateResult
    fun startUpdateFlow(updateResult: UpdateResult, context: Any?)
}

class UpdateViewModel : ViewModel() {

    private val updateChecker = UpdateChecker()

    private val _state = MutableStateFlow<UpdateUIState>(UpdateUIState.Idle)
    val state = _state.asStateFlow()

    fun checkForUpdate(context: Any?) {
        if (_state.value is UpdateUIState.Loading) return

        _state.value = UpdateUIState.Loading

        viewModelScope.launch {
            _state.value = UpdateUIState.Loading
            try {
                val result = updateChecker.checkForUpdate(context)
                _state.value = UpdateUIState.Success(result)
            } catch (e: Exception) {
                _state.value = UpdateUIState.Error(
                    e.message ?: "An error occurred while checking for updates."
                )
            }
        }
    }

    fun startUpdateFlow(context: Any?) {
        viewModelScope.launch {
            updateChecker.startUpdateFlow(
                (_state.value as? UpdateUIState.Success)?.result ?: UpdateResult(false), context
            )
        }
    }
}
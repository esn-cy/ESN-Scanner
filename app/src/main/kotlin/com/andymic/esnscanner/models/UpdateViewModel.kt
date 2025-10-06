package com.andymic.esnscanner.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UpdateUIState {
    data object Idle : UpdateUIState
    data object Loading : UpdateUIState
    data class Success(val result: UpdateResult) : UpdateUIState
    data class Error(val error: Exception? = null) : UpdateUIState
}

data class UpdateResult(
    var isUpdateAvailable: Boolean,
    var updateInfo: AppUpdateInfo
)

class UpdateViewModel(application: Application) : AndroidViewModel(application) {
    private var appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(application)

    private val _state = MutableStateFlow<UpdateUIState>(UpdateUIState.Idle)
    val state = _state.asStateFlow()

    private val _updateEvent = MutableSharedFlow<AppUpdateInfo>()
    val updateEvent = _updateEvent.asSharedFlow()

    private var appUpdateInfo: AppUpdateInfo? = null

    init {
        checkForUpdate()
    }

    private fun checkForUpdate() {
        if (_state.value is UpdateUIState.Loading) return

        _state.value = UpdateUIState.Loading

        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateReady = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (isUpdateReady) {
                this.appUpdateInfo = info
                _state.value = UpdateUIState.Success(
                    result = UpdateResult(
                        isUpdateAvailable = true,
                        updateInfo = info
                    )
                )
            }
        }.addOnFailureListener { e ->
            _state.value = UpdateUIState.Error(e)
        }
    }

    fun startUpdateFlow() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is UpdateUIState.Success && currentState.result.isUpdateAvailable) {
                _updateEvent.emit(currentState.result.updateInfo)
            }
        }
    }
}
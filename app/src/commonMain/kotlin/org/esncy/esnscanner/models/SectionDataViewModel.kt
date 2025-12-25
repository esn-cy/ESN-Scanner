package org.esncy.esnscanner.models

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.esncy.esnscanner.dataStore

@Serializable
data class SectionData(
    var localSectionName: String = "",
    var localSectionCode: String = "",
    var localSectionDomain: String = "",
    var spreadsheetID: String = ""
)

sealed interface SectionDataUIState {
    data object Loading : SectionDataUIState
    data class Success(val result: SectionData) : SectionDataUIState
    data class Error(val error: Exception? = null) : SectionDataUIState
}

class SectionDataViewModel() : ViewModel() {
    private val KEY_LOCAL_SECTION_NAME = stringPreferencesKey("localSectionName")
    private val KEY_LOCAL_SECTION_CODE = stringPreferencesKey("localSectionCode")
    private val KEY_LOCAL_SECTION_DOMAIN = stringPreferencesKey("localSectionDomain")
    private val KEY_SPREADSHEET_ID = stringPreferencesKey("spreadsheetID")

    val state: StateFlow<SectionDataUIState> = dataStore.data
        .map { prefs ->
            SectionData(
                localSectionName = prefs[KEY_LOCAL_SECTION_NAME] ?: "",
                localSectionCode = prefs[KEY_LOCAL_SECTION_CODE] ?: "",
                localSectionDomain = prefs[KEY_LOCAL_SECTION_DOMAIN] ?: "",
                spreadsheetID = prefs[KEY_SPREADSHEET_ID] ?: ""
            )
        }
        .map { SectionDataUIState.Success(it) as SectionDataUIState }
        .catch { emit(SectionDataUIState.Error(it as Exception?)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SectionDataUIState.Loading
        )

    val dataFlow: StateFlow<SectionData> = state
        .map { (it as? SectionDataUIState.Success)?.result ?: SectionData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SectionData()
        )

    fun updateData(
        localSectionName: String? = null,
        localSectionCode: String? = null,
        localSectionDomain: String? = null,
        spreadsheetID: String? = null
    ) {
        try {
            viewModelScope.launch {
                dataStore.edit { preferences ->
                    localSectionName?.let { preferences[KEY_LOCAL_SECTION_NAME] = it }
                    localSectionCode?.let { preferences[KEY_LOCAL_SECTION_CODE] = it }
                    localSectionDomain?.let { preferences[KEY_LOCAL_SECTION_DOMAIN] = it }
                    spreadsheetID?.let { preferences[KEY_SPREADSHEET_ID] = it }
                }
            }
        } catch (_: Exception) {
        }
    }
}
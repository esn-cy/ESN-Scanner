package org.esncy.esnscanner.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esnscanner.app.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.esncy.esnscanner.data.ApiServiceImplementation
import org.esncy.esnscanner.data.DatasetResponse
import org.esncy.esnscanner.data.InternationalResponse
import org.esncy.esnscanner.data.KtorClient
import org.esncy.esnscanner.data.Section
import org.esncy.esnscanner.data.dateFormat
import org.esncy.esnscanner.data.getCardStatus
import org.esncy.esnscanner.data.getExpirationDate
import org.esncy.esnscanner.data.getIssuingSection
import org.esncy.esnscanner.data.getLastScanDate
import org.esncy.esnscanner.ui.screens.CameraViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

sealed interface ScanUIState {
    data object Idle : ScanUIState
    data object Loading : ScanUIState
    data class Success(val result: ScanResult, val lastScan: String) : ScanUIState
    data class Error(val message: String, val lastScan: String) : ScanUIState
}

data class ScanResult(
    var identifier: String,
    var fullName: String,
    var nationality: String,
    var lastScanDate: String,
    var result: String,
    var cardStatus: String,
    var issuingSection: String,
    var expirationDate: String,
    var profileImageURL: String
)

val ESNCardNumberRegex = Regex("\\d\\d\\d\\d\\d\\d\\d[A-Z][A-Z][A-Z][A-Z0-9]")
val ESNCyprusPassRegex = Regex("ESNCYTKNESNCYTKN\\d*")

class ScanViewModel(
    private val dataFlow: StateFlow<SectionData>
) : ViewModel(), CameraViewModel<ScanUIState> {
    val sectionData: SectionData
        get() = dataFlow.value

    private val apiService: ApiServiceImplementation
        get() = ApiServiceImplementation(
            client = KtorClient.httpClient,
            sectionDomain = sectionData.localSectionDomain,
            spreadsheetID = sectionData.spreadsheetID,
        )

    lateinit var sections: List<Section>

    init {
        viewModelScope.launch {
            sections = Json.decodeFromString<List<Section>>(
                Res.readBytes("files/sections.json").decodeToString()
            )
        }
    }

    private val _state = MutableStateFlow<ScanUIState>(ScanUIState.Idle)
    override val state = _state.asStateFlow()

    @OptIn(ExperimentalTime::class)
    override fun onScan(scannedString: String) {
        if (_state.value is ScanUIState.Loading) return

        val lastScan =
            if (_state.value is ScanUIState.Success) (_state.value as ScanUIState.Success).lastScan else if (_state.value is ScanUIState.Error) (_state.value as ScanUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _state.value = ScanUIState.Loading

        var identifier: String
        val esncardMatch = ESNCardNumberRegex.find(scannedString)
        val isESNcard = esncardMatch != null
        if (isESNcard)
            identifier = esncardMatch.value
        else {
            val esnCyprusPassMatch = ESNCyprusPassRegex.find(scannedString)
            if (esnCyprusPassMatch == null) {
                _state.value = ScanUIState.Error("Invalid", scannedString)
                return
            }
            identifier = esnCyprusPassMatch.value
        }

        viewModelScope.launch {
            val localInfo = apiService.getLocalInfo(identifier)
            var internationalInfo: InternationalResponse? = null
            var datasetInfo: DatasetResponse? = null
            if (isESNcard) {
                internationalInfo = apiService.getInternationalInfo(identifier)
                datasetInfo = apiService.getDatasetInfo(identifier)
            }

            val existsLocally = datasetInfo != null || localInfo != null

            val fullName = datasetInfo?.name
                ?: (if (localInfo != null) localInfo.name.trim() + " " + localInfo.surname.trim() else "UNKNOWN")
            val nationality = datasetInfo?.nationality ?: (localInfo?.nationality ?: "UNKNOWN")
            val issuingSection = if (isESNcard) getIssuingSection(
                internationalInfo?.sectionCode?.lowercase(),
                existsLocally,
                sections,
                sectionData
            ) else sectionData.localSectionName
            val expirationDate = if (isESNcard) getExpirationDate(
                localInfo?.paidDate,
                datasetInfo?.date,
                internationalInfo?.expirationDate
            ) else "Valid"
            val cardStatus = getCardStatus(internationalInfo?.status, existsLocally)
            val lastScan = getLastScanDate(localInfo)
            val profileImage = localInfo?.profileImageURL ?: "UNKNOWN"

            var result: String
            val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val epochDate =
                LocalDate(currentDate.year, currentDate.month, currentDate.day).toEpochDays()
            if (isESNcard) {
                result =
                    if (issuingSection == sectionData.localSectionName && !issuingSection.contains("(INCONSISTENT)")) {
                        if (existsLocally)
                            "Valid"
                        else
                            "Not in our System"
                    } else {
                        if (existsLocally)
                            "Valid"
                        else {
                            if (issuingSection != "NOT REGISTERED")
                                "Valid Foreign Card"
                            else
                                "Unknown Origin"
                        }
                    }

                if (lastScan != "UNKNOWN" && lastScan != "Never Scanned" && epochDate - LocalDate.parse(
                        lastScan,
                        dateFormat
                    ).toEpochDays() < 2
                ) {
                    result = "Already Scanned"
                }

                if (expirationDate != "Valid" && expirationDate != "UNKNOWN" && LocalDate.parse(
                        expirationDate.removeSuffix(" (INCONSISTENT)"),
                        dateFormat
                    ).toEpochDays() < epochDate
                )
                    result = "Expired"
            } else {
                result =
                    if (lastScan == "UNKNOWN" || lastScan == "Never Scanned" || epochDate - LocalDate.parse(
                            lastScan,
                            dateFormat
                        ).toEpochDays() > 1
                    )
                        "Valid"
                    else
                        "Already Scanned"
//                if (epochDate < LocalDate.parse(
//                        expirationDate.removeSuffix(" (INCONSISTENT)"),
//                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
//                    ).toEpochDay()
//                )
//                    result = "Expired"
            }
            if (result != "Expired" && cardStatus == "NOT REGISTERED")
                result += "/Not Registered"
            if (cardStatus != "NOT REGISTERED" && (expirationDate.contains("INCONSISTENT") || issuingSection.contains(
                    "INCONSISTENT"
                ))
            )
                result += "/Inconsistent"

            _state.value = ScanUIState.Success(
                ScanResult(
                    identifier = identifier,
                    fullName = fullName,
                    nationality = nationality,
                    lastScanDate = lastScan,
                    result = result,
                    cardStatus = cardStatus,
                    issuingSection = issuingSection,
                    expirationDate = expirationDate,
                    profileImageURL = profileImage
                ), scannedString
            )
        }
    }
}
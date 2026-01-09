package org.esncy.esnscanner.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esnscanner.app.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.plus
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
import org.esncy.esnscanner.data.getOptionalDate
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
    var cardStatus: String,
    var issuingSection: String,
    var expirationDate: String,
    var profileImageURL: String,
    var result: String
)

val ESNCardNumberRegex = Regex("\\d\\d\\d\\d\\d\\d\\d[A-Z][A-Z][A-Z][A-Z0-9]")
val FreePassRegex = Regex("^[A-F0-9]{32}$")

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
            val freePassMatch = FreePassRegex.find(scannedString)
            if (freePassMatch == null) {
                _state.value = ScanUIState.Error("Invalid", scannedString)
                return
            }
            identifier = freePassMatch.value
        }

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val epochDate =
            LocalDate(currentDate.year, currentDate.month, currentDate.day).toEpochDays()
        
        viewModelScope.launch {
            val localInfo = apiService.getLocalInfo(identifier)
            var internationalInfo: InternationalResponse?
            var datasetInfo: DatasetResponse?

            var fullName: String
            var nationality: String
            var issuingSection: String
            var expirationDate: String
            var cardStatus: String
            var lastScan: String
            var profileImage: String
            var result: String

            if (localInfo?.name == "BLACKLISTED" && localInfo.surname == "BLACKLISTED") {
                _state.value = ScanUIState.Error("Blacklisted", identifier)
                return@launch
            }

            if (isESNcard) {
                internationalInfo = apiService.getInternationalInfo(identifier)
                datasetInfo = apiService.getDatasetInfo(identifier)

                val existsLocally = datasetInfo != null || localInfo != null

                fullName = datasetInfo?.name
                    ?: (if (localInfo != null) localInfo.name.trim() + " " + localInfo.surname.trim() else "UNKNOWN")
                nationality = datasetInfo?.nationality ?: (localInfo?.nationality ?: "UNKNOWN")
                issuingSection = getIssuingSection(
                    internationalInfo?.sectionCode?.lowercase(),
                    existsLocally,
                    sections,
                    sectionData
                )
                expirationDate = getExpirationDate(
                    localInfo?.datePaid,
                    datasetInfo?.date,
                    internationalInfo?.expirationDate
                )
                cardStatus = getCardStatus(internationalInfo?.status, existsLocally)
                lastScan = getOptionalDate(localInfo?.lastScanDate)
                profileImage = localInfo?.profileImageURL ?: "UNKNOWN"

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

                if (
                    lastScan != "UNKNOWN" &&
                    lastScan != "Not Populated" &&
                    epochDate - dateFormat.parse(lastScan).toEpochDays() < 2
                ) {
                    result = "Already Scanned"
                }

                if (expirationDate != "UNKNOWN" && dateFormat.parse(
                        expirationDate.removeSuffix(" (INCONSISTENT)")
                    ).toEpochDays() < epochDate
                )
                    result = "Expired"
            } else {
                if (localInfo == null) {
                    _state.value = ScanUIState.Error("Local Data Unavailable", scannedString)
                    return@launch
                }

                fullName = localInfo.name.trim() + " " + localInfo.surname.trim()
                nationality = localInfo.nationality
                issuingSection = sectionData.localSectionName

                val expirationDateObject = LocalDate
                    .parse(localInfo.dateApproved!!)
                    .plus(1, DateTimeUnit.YEAR)
                expirationDate = expirationDateObject.format(dateFormat)

                cardStatus =
                    if (epochDate > expirationDateObject.toEpochDays()) "Expired" else "Valid"

                lastScan = getOptionalDate(localInfo.lastScanDate)

                result = localInfo.mobilityStatus

                if (
                    lastScan != "UNKNOWN" &&
                    lastScan != "Not Populated" &&
                    epochDate - dateFormat.parse(lastScan).toEpochDays() < 2
                ) {
                    result = "Already Scanned"
                }

                if (cardStatus == "Expired")
                    result = "Expired"

                profileImage =
                    if (cardStatus == "Valid" && result != "Already Scanned") "Valid" else "Invalid"
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
                    cardStatus = cardStatus,
                    issuingSection = issuingSection,
                    expirationDate = expirationDate,
                    profileImageURL = profileImage,
                    result = result,
                ), scannedString
            )
        }
    }
}
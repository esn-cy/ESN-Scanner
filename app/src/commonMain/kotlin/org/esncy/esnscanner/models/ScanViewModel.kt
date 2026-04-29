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
import org.esncy.esnscanner.data.APIService
import org.esncy.esnscanner.data.KtorClients
import org.esncy.esnscanner.data.LocalGuestResponse
import org.esncy.esnscanner.data.LocalPassResponse
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

interface ScanResult {
    var identifier: String
    var fullName: String
    var profileImageURL: String
    var result: String
}

data class PassScanResult(
    override var identifier: String,
    override var fullName: String,
    var nationality: String,
    var lastScanDate: String,
    var cardStatus: String,
    var issuingSection: String,
    var expirationDate: String,
    override var profileImageURL: String,
    override var result: String
) : ScanResult

data class GuestScanResult(
    override var identifier: String,
    override var fullName: String,
    val refererFullName: String,
    val refererMobilityStatus: String,
    val dateRedeemed: String,
    override var profileImageURL: String,
    override var result: String
) : ScanResult

enum class ScanTypes(
    val regex: Regex
) {
    ESNcard(Regex("\\d\\d\\d\\d\\d\\d\\d[A-Z][A-Z][A-Z][A-Z0-9]")),
    FreePass(Regex("^[A-F0-9]{32}$")),
    GuestPass(Regex("^GUEST[A-F0-9]{27}$"));

    companion object {
        fun getType(scannedString: String): Pair<ScanTypes?, String?> {
            var identifier: String? = null
            val type = ScanTypes.entries.find {
                val currentFind = it.regex.find(scannedString)
                if (currentFind?.value == null)
                    return@find false

                identifier = currentFind.value

                var existsElsewhere = false
                for (otherEntry in ScanTypes.entries) {
                    if (otherEntry == it)
                        continue
                    if (otherEntry.regex.find(scannedString) != null) {
                        existsElsewhere = true
                        break
                    }
                }
                return@find !existsElsewhere
            }
            if (type == null)
                identifier = null
            return Pair(type, identifier)
        }
    }
}

class ScanViewModel(
    private val dataFlow: StateFlow<SectionData>,
    private val clients: KtorClients
) : ViewModel(), CameraViewModel<ScanUIState> {
    val sectionData: SectionData
        get() = dataFlow.value

    private val apiService: APIService
        get() = APIService(
            publicClient = clients.publicClient,
            privateClient = clients.privateClient,
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

        val (type, identifier) = ScanTypes.getType(scannedString)

        if (identifier == null || type == null) {
            _state.value = ScanUIState.Error("Invalid", scannedString)
            return
        }

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val epochDate =
            LocalDate(currentDate.year, currentDate.month, currentDate.day).toEpochDays()
        
        viewModelScope.launch {
            val localInfo = apiService.getLocalInfo(identifier)

            if (localInfo?.name == "BLACKLISTED" && localInfo.surname == "BLACKLISTED") {
                _state.value = ScanUIState.Error("Blacklisted", identifier)
                return@launch
            }

            var fullName =
                if (localInfo != null) localInfo.name.trim() + " " + localInfo.surname.trim() else "UNKNOWN"

            val scanResult = when (type) {
                ScanTypes.ESNcard -> {
                    val local = localInfo as? LocalPassResponse?
                    val internationalInfo = apiService.getInternationalInfo(identifier)
                    val datasetInfo = apiService.getDatasetInfo(identifier)

                    val existsLocally = datasetInfo != null || local != null

                    val nationality = if (datasetInfo != null) {
                        fullName = datasetInfo.name
                        datasetInfo.nationality
                    } else
                        local?.nationality ?: "UNKNOWN"

                    val issuingSection = getIssuingSection(
                        internationalInfo?.sectionCode?.lowercase(),
                        existsLocally,
                        sections,
                        sectionData
                    )

                    val expirationDate = getExpirationDate(
                        local?.datePaid,
                        datasetInfo?.date,
                        internationalInfo?.expirationDate
                    )
                    val cardStatus = getCardStatus(internationalInfo?.status, existsLocally)

                    val lastScan = getOptionalDate(local?.lastScanDate)
                    val profileImage = local?.profileImageURL ?: "UNKNOWN"
                    var result =
                        if (issuingSection == sectionData.localSectionName && !issuingSection.contains(
                                "(INCONSISTENT)"
                            )
                        ) {
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

                    if (result != "Expired" && cardStatus == "NOT REGISTERED")
                        result += "/Not Registered"
                    if (cardStatus != "NOT REGISTERED" && (expirationDate.contains("INCONSISTENT") || issuingSection.contains(
                            "INCONSISTENT"
                        ))
                    )
                        result += "/Inconsistent"

                    PassScanResult(
                        identifier = identifier,
                        fullName = fullName,
                        nationality = nationality,
                        lastScanDate = lastScan,
                        cardStatus = cardStatus,
                        issuingSection = issuingSection,
                        expirationDate = expirationDate,
                        profileImageURL = profileImage,
                        result = result,
                    )
                }

                ScanTypes.FreePass -> {
                    if (localInfo == null) {
                        _state.value = ScanUIState.Error("Local Data Unavailable", scannedString)
                        return@launch
                    }

                    val local = localInfo as? LocalPassResponse
                    if (local == null) {
                        _state.value = ScanUIState.Error("Invalid Data", scannedString)
                        return@launch
                    }

                    val nationality = local.nationality
                    val issuingSection = sectionData.localSectionName

                    var expirationDate: String
                    var cardStatus: String
                    if (local.dateApproved != null) {
                        val expirationDateObject = LocalDate
                            .parse(local.dateApproved)
                            .plus(1, DateTimeUnit.YEAR)
                        expirationDate = expirationDateObject.format(dateFormat)

                        cardStatus =
                            if (epochDate > expirationDateObject.toEpochDays()) "Expired" else "Valid"
                    } else {
                        expirationDate = "UNKNOWN"
                        cardStatus = "UNKNOWN"
                    }

                    val lastScan = getOptionalDate(local.lastScanDate)

                    var result = local.mobilityStatus

                    if (
                        lastScan != "UNKNOWN" &&
                        lastScan != "Not Populated" &&
                        epochDate - dateFormat.parse(lastScan).toEpochDays() < 2
                    ) {
                        result = "Already Scanned"
                    }

                    if (cardStatus == "Expired")
                        result = "Expired"

                    val profileImage = local.profileImageURL
                        ?: if (cardStatus == "Valid" && result != "Already Scanned") "Valid" else "Invalid"

                    PassScanResult(
                        identifier = identifier,
                        fullName = fullName,
                        nationality = nationality,
                        lastScanDate = lastScan,
                        cardStatus = cardStatus,
                        issuingSection = issuingSection,
                        expirationDate = expirationDate,
                        profileImageURL = profileImage,
                        result = result,
                    )
                }

                ScanTypes.GuestPass -> {
                    if (localInfo == null) {
                        _state.value = ScanUIState.Error("Local Data Unavailable", scannedString)
                        return@launch
                    }

                    val local = localInfo as? LocalGuestResponse
                    if (local == null) {
                        _state.value = ScanUIState.Error("Invalid Data", scannedString)
                        return@launch
                    }

                    val dateRedeemed =
                        if (local.dateRedeemed == null) "Valid" else (LocalDate.parse(local.dateRedeemed)
                            .format(dateFormat) + " (REDEEMED)")

                    var profileImageURL: String
                    var status: String
                    if (dateRedeemed == "Valid") {
                        profileImageURL = "Valid"
                        status = "Valid"
                    } else {
                        profileImageURL = "Invalid"
                        status = "Already Redeemed"
                    }

                    GuestScanResult(
                        identifier = identifier,
                        fullName = fullName,
                        refererFullName = local.refererName.trim() + " " + local.refererSurname.trim(),
                        refererMobilityStatus = local.refererMobilityStatus,
                        dateRedeemed = dateRedeemed,
                        profileImageURL = profileImageURL,
                        result = status,
                    )
                }
            }

            _state.value = ScanUIState.Success(
                scanResult, scannedString
            )
        }
    }
}
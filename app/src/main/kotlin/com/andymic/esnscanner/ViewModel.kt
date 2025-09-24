package com.andymic.esnscanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.ApiServiceImplementation
import com.andymic.esnscanner.data.DatasetResponse
import com.andymic.esnscanner.data.InternationalResponse
import com.andymic.esnscanner.data.KtorClient
import com.andymic.esnscanner.data.SectionData
import com.andymic.esnscanner.data.Sections
import com.andymic.esnscanner.data.getCardStatus
import com.andymic.esnscanner.data.getExpirationDate
import com.andymic.esnscanner.data.getIssuingSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    val sections: List<Sections.Section>
    val sectionData: SectionData.SectionData

    init {
        val sectionsInputStream = application.assets.open("sections.json")
        sections = Sections(sectionsInputStream).sections
        val sectionDataInputStream = application.assets.open("section-data.json")
        sectionData = SectionData(sectionDataInputStream).sectionData
    }

    private val apiService = ApiServiceImplementation(KtorClient.httpClient)

    private val _scanState = MutableStateFlow<ScanUIState>(ScanUIState.Idle)
    val scanState = _scanState.asStateFlow()

    fun scanRequest(scannedString: String) {
        if (_scanState.value is ScanUIState.Loading) return

        val lastScan =
            if (_scanState.value is ScanUIState.Success) (_scanState.value as ScanUIState.Success).lastScan else if (_scanState.value is ScanUIState.Error) (_scanState.value as ScanUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _scanState.value = ScanUIState.Loading

        var identifier = scannedString
        val esncardMatch = ESNCardNumberRegex.find(scannedString)
        val isESNcard = esncardMatch != null
        if (isESNcard)
            identifier = esncardMatch.value
        else {
            val esnCyprusPassMatch = ESNCyprusPassRegex.find(scannedString)
            if (esnCyprusPassMatch == null) {
                _scanState.value = ScanUIState.Error("Invalid", scannedString)
                return
            }
        }

        viewModelScope.launch {
            val localInfo = apiService.getLocalInfo(identifier)
            var internationalInfo: InternationalResponse? = null
            var datasetInfo: DatasetResponse? = null
            if (isESNcard) {
                internationalInfo = apiService.getInternationalInfo(identifier)
                datasetInfo = apiService.getDatasetInfo(identifier, sectionData.spreadsheetID)
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
            val lastScan = if (localInfo != null) LocalDate.parse(localInfo.lastScanDate)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "UNKNOWN"
            val profileImage = localInfo?.profileImageURL ?: "UNKNOWN"

            var result: String
            val epochDate = LocalDate.now().toEpochDay()
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

                if (lastScan != "UNKNOWN" && epochDate - LocalDate.parse(
                        lastScan,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ).toEpochDay() < 2
                ) {
                    result = "Already Scanned"
                }


                if (expirationDate != "Valid" && LocalDate.parse(
                        expirationDate.removeSuffix(" (INCONSISTENT)"),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ).toEpochDay() < epochDate
                )
                    result = "Expired"
            } else {
                result =
                    if (lastScan == null || epochDate - LocalDate.parse(
                            lastScan,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ).toEpochDay() > 1
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

            _scanState.value = ScanUIState.Success(
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
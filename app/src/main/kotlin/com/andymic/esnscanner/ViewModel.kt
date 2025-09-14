package com.andymic.esnscanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.andymic.esnscanner.data.ApiServiceImplementation
import com.andymic.esnscanner.data.InternationalResponse
import com.andymic.esnscanner.data.KtorClient
import com.andymic.esnscanner.data.Sections
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

data class ScanResult (
    var identifier: String,
    var name: String,
    var surname: String,
    var homeCountry: String,
    var lastScanDate: String,
    var result: String,
    var cardStatus: String,
    var issuingSection: String,
    var expirationDate: String,
    var profileImageURL: String
)

val cardNumberRegex = Regex("\\d\\d\\d\\d\\d\\d\\d[A-Z][A-Z][A-Z][A-Z0-9]")

class ScanViewModel(application: Application): AndroidViewModel(application) {
    private val apiService = ApiServiceImplementation(KtorClient.httpClient)

    private val _scanState = MutableStateFlow<ScanUIState>(ScanUIState.Idle)
    val scanState = _scanState.asStateFlow()

    fun scanRequest(scannedString: String) {
        if (_scanState.value is ScanUIState.Loading) return

        val lastScan = if (_scanState.value is ScanUIState.Success) (_scanState.value as ScanUIState.Success).lastScan else if (_scanState.value is ScanUIState.Error) (_scanState.value as ScanUIState.Error).lastScan else null
        if (lastScan == scannedString) return

        _scanState.value = ScanUIState.Loading

        var identifier = scannedString
        val match = cardNumberRegex.find(scannedString)
        val isESNcard = match != null
        if (isESNcard)
            identifier = match.value

        viewModelScope.launch {
            val localInfo = apiService.getLocalInfo(identifier)
            var internationalInfo: InternationalResponse? = null
            if (isESNcard) {
                internationalInfo = apiService.getInternationalInfo(identifier)
            }

            if (internationalInfo != null) {
                var issuingSection: String
                var cardStatus: String
                var expirationDate: String

                if (internationalInfo.status != "available") {
                    cardStatus =
                        internationalInfo.status[0].uppercase() + internationalInfo.status.substring(
                            1
                        )
                    issuingSection = "UNKNOWN"
                    expirationDate = LocalDate.parse(internationalInfo.expirationDate)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    if (internationalInfo.sectionCode.lowercase() == "cy-nico-esa")
                        issuingSection = "ESN Nicosia"
                    else {
                        for (section in Sections(application).sections) {
                            if (section.code == internationalInfo.sectionCode.lowercase()) {
                                issuingSection = section.name
                                break
                            }
                        }
                    }
                } else {
                    cardStatus = "NOT REGISTERED"
                    issuingSection = "N/A"
                    expirationDate = "N/A"
                }

                var result: String

                val response = ScanResult(
                    identifier = identifier,
                    name = "UNKNOWN",
                    surname = "UNKNOWN",
                    homeCountry = "UNKNOWN",
                    lastScanDate = "UNKNOWN",
                    result = "TBD",
                    cardStatus = cardStatus,
                    issuingSection = issuingSection,
                    expirationDate = expirationDate,
                    profileImageURL = "UNKNOWN"
                )

                if (localInfo != null) {
                    response.name = localInfo.name
                    response.surname = localInfo.surname
                    response.homeCountry = localInfo.homeCountry
                    response.lastScanDate = LocalDate.parse(localInfo.lastScanDate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    response.profileImageURL = localInfo.profileImageURL

                    result = if (LocalDate.now().toEpochDay() - LocalDate.parse(localInfo.lastScanDate).toEpochDay() > 1)
                        if (cardStatus != "NOT REGISTERED") "Valid" else "Valid/Not Registered"
                    else
                        "Already Scanned"
                } else {
                    result = if (cardStatus != "NOT REGISTERED") {
                        if (issuingSection == "ESN Nicosia")
                            "Not in our System"
                        else
                            "Foreign Card"
                    } else
                        "Not in our System/Not Verified"
                }
                if (internationalInfo.expirationDate != "N/A" && LocalDate.now()
                        .toEpochDay() > LocalDate.parse(internationalInfo.expirationDate)
                        .toEpochDay()
                )
                    result = "Expired"
                response.result = result
                _scanState.value = ScanUIState.Success(response, scannedString)
            } else {
                if (localInfo != null) {
                    val response = ScanResult(
                        identifier = identifier,
                        name = localInfo.name,
                        surname = localInfo.surname,
                        homeCountry = localInfo.homeCountry,
                        lastScanDate = localInfo.lastScanDate,
                        result = "TBD",
                        cardStatus = "UNKNOWN",
                        issuingSection = "ESN Nicosia",
                        expirationDate = "UNKNOWN", //TODO Expiration date
                        profileImageURL = localInfo.profileImageURL
                    )

                    val result: String = if (isESNcard)
                        "Valid Locally. Check Number"
                    else
                        "Valid"
                    response.result = result
                    _scanState.value = ScanUIState.Success(response, scannedString)
                } else {
                    _scanState.value = ScanUIState.Error("Not Found/Invalid.", scannedString)
                    return@launch
                }
            }
        }
    }
}
package com.andymic.esnscanner.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getCardStatus(internationalStatus: String?, existsLocally: Boolean): String {
    return when (internationalStatus) {
        "available" -> "NOT REGISTERED"
        "active" -> "Valid"
        "expired" -> "EXPIRED"
        null -> {
            if (existsLocally)
                "Valid Locally. Check Number"
            else
                "UNKNOWN"
        }

        else -> "UNKNOWN PATH"
    }
}

fun getIssuingSection(
    internationalSection: String?,
    existsLocally: Boolean,
    sections: List<Sections.Section>,
    sectionData: SectionData.SectionData
): String {
    if (internationalSection == null)
        return "NOT REGISTERED"

    var issuingSection: String? = null

    if (internationalSection == sectionData.localSectionCode) {
        issuingSection = sectionData.localSectionName
    } else {
        for (section in sections) {
            if (section.code == internationalSection) {
                issuingSection = section.name
                break
            }
        }
        if (existsLocally)
            issuingSection += " (INCONSISTENT)"
    }

    return issuingSection ?: "SECTION NOT FOUND"
}

fun getExpirationDate(
    localPaidDate: String?,
    datasetDate: String?,
    internationalExpiration: String?
): String {
    var reliableExpirationDate: LocalDate? = null
    var internationalExpirationDate: LocalDate? = null
    var datasetExpirationDate: LocalDate?
    var localExpirationDate: LocalDate?

    if (internationalExpiration != null) {
        internationalExpirationDate = LocalDate.parse(internationalExpiration)
        reliableExpirationDate = internationalExpirationDate
    }
    if (datasetDate != null) {
        datasetExpirationDate =
            LocalDate.parse(datasetDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).plusYears(1)
        reliableExpirationDate = datasetExpirationDate
    }
    if (localPaidDate != null) {
        localExpirationDate = LocalDate.parse(localPaidDate).plusYears(1)
        reliableExpirationDate = localExpirationDate
    }

    if (reliableExpirationDate != null) {
        var formatted = reliableExpirationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        if (internationalExpirationDate != null && reliableExpirationDate.toEpochDay() < internationalExpirationDate.toEpochDay())
            formatted += " (INCONSISTENT)"

        return formatted
    } else
        return "UNKNOWN"


}
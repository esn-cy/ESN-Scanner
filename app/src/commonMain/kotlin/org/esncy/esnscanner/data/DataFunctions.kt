package org.esncy.esnscanner.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.plus
import org.esncy.esnscanner.models.SectionData

fun getCardStatus(internationalStatus: String?, existsLocally: Boolean): String {
    return when (internationalStatus) {
        "available" -> "NOT REGISTERED"
        "active" -> "Valid"
        "expired" -> "EXPIRED"
        null -> {
            if (existsLocally)
                "Valid Locally"
            else
                "UNKNOWN"
        }

        else -> "UNKNOWN PATH"
    }
}

fun getIssuingSection(
    internationalSection: String?,
    existsLocally: Boolean,
    sections: List<Section>,
    sectionData: SectionData
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

val dateFormat = LocalDate.Format {
    day(Padding.ZERO)
    char('/')
    monthNumber(Padding.ZERO)
    char('/')
    year(Padding.ZERO)
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
            LocalDate.parse(datasetDate, dateFormat).plus(1, DateTimeUnit.YEAR)
        reliableExpirationDate = datasetExpirationDate
    }
    if (localPaidDate != null) {
        localExpirationDate = LocalDate.parse(localPaidDate).plus(1, DateTimeUnit.YEAR)
        reliableExpirationDate = localExpirationDate
    }

    if (reliableExpirationDate != null) {
        var formatted = reliableExpirationDate.format(dateFormat)

        if (internationalExpirationDate != null && reliableExpirationDate.toEpochDays() < internationalExpirationDate.toEpochDays())
            formatted += " (INCONSISTENT)"

        return formatted
    } else
        return "UNKNOWN"
}

fun getLastScanDate(localInfo: LocalResponse?): String {
    return if (localInfo != null) {
        if (localInfo.lastScanDate != null && localInfo.lastScanDate != "") {
            LocalDate.parse(localInfo.lastScanDate).format(dateFormat)
        } else {
            "Never Scanned"
        }
    } else {
        "UNKNOWN"
    }
}
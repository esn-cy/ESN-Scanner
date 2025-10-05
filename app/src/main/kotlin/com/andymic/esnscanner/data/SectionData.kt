package com.andymic.esnscanner.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.InputStream

class SectionData(inputStream: InputStream) {
    @Serializable
    data class SectionData(
        var spreadsheetID: String,
        var localSectionName: String,
        var localSectionCode: String,
        var localSectionDomain: String
    )

    var sectionData: SectionData

    init {
        val sectionsJSON = Json.parseToJsonElement(inputStream.bufferedReader().use {
            it.readText()
        })

        sectionData = SectionData(
            spreadsheetID = sectionsJSON.jsonObject["spreadsheetID"].toString().drop(1).dropLast(1),
            localSectionName = sectionsJSON.jsonObject["localSectionName"].toString().drop(1)
                .dropLast(1),
            localSectionCode = sectionsJSON.jsonObject["localSectionCode"].toString().drop(1)
                .dropLast(1),
            sectionsJSON.jsonObject["localSectionDomain"].toString().drop(1)
                .dropLast(1)
        )
    }
}
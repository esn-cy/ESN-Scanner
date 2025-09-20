package com.andymic.esnscanner.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.InputStream

class Sections(inputStream: InputStream) {

    @Serializable
    data class Section(
        var code: String,
        var name: String,
        var city: String
    )

    var sections: List<Section>

    init {
        val sectionsJSON = Json.parseToJsonElement(inputStream.bufferedReader().use {
            it.readText()
        })

        val list = sectionsJSON.jsonArray.toList()
        var newList: MutableList<Section> = mutableListOf()

        for (section in list) {
            newList.add(Section(
                code = section.jsonObject["code"].toString().drop(1).dropLast(1),
                name = section.jsonObject["name"].toString().drop(1).dropLast(1),
                city = section.jsonObject["city"].toString().drop(1).dropLast(1)
            ))
        }
        sections = newList.toList()
    }
}
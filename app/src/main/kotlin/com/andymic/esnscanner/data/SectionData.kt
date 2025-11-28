package com.andymic.esnscanner.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SectionData {
    @Serializable
    data class SectionData(
        var localSectionName: String = "",
        var localSectionCode: String = "",
        var localSectionDomain: String = "",
        var spreadsheetID: String = ""
    )

    object SectionDataSerializer : Serializer<SectionData> {
        override val defaultValue: SectionData = SectionData()

        override suspend fun readFrom(input: InputStream): SectionData =
            try {
                Json.decodeFromString<SectionData>(
                    input.readBytes().decodeToString()
                )
            } catch (serialization: SerializationException) {
                throw CorruptionException("Unable to read Settings", serialization)
            }

        override suspend fun writeTo(t: SectionData, output: OutputStream) {
            output.write(
                Json.encodeToString(t)
                    .encodeToByteArray()
            )
        }
    }
}
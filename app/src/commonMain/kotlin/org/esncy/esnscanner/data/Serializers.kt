package org.esncy.esnscanner.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object LocalSerializer : KSerializer<LocalResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LocalResponse")

    override fun serialize(encoder: Encoder, value: LocalResponse) {
        when (value) {
            is LocalPassResponse -> encoder.encodeSerializableValue(
                LocalPassResponse.serializer(),
                value
            )

            is LocalGuestResponse -> encoder.encodeSerializableValue(
                LocalGuestResponse.serializer(),
                value
            )

            else -> throw SerializationException("Unknown subclass of LocalResponse")
        }
    }

    override fun deserialize(decoder: Decoder): LocalResponse {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer can only be used with JSON")

        val jsonElement = jsonDecoder.decodeJsonElement()
        val jsonObject = jsonElement.jsonObject

        return when {
            "nationality" in jsonObject -> {
                decoder.json.decodeFromJsonElement(LocalPassResponse.serializer(), jsonElement)
            }

            "refererName" in jsonObject -> {
                decoder.json.decodeFromJsonElement(LocalGuestResponse.serializer(), jsonElement)
            }

            else -> throw SerializationException("Unknown JSON structure for LocalResponse")
        }
    }
}

object InternationalSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("InternationalSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String?) {
        if (value != null)
            encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String? {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer can only be used with JSON")
        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> jsonElement.content
            is JsonArray -> {
                if (jsonElement.size == 1 && jsonElement.firstOrNull()?.jsonPrimitive?.content == "") {
                    null
                } else {
                    throw SerializationException("Array must contain a single empty string.")
                }
            }

            else -> throw SerializationException("Unexpected JSON element type.")
        }
    }
}
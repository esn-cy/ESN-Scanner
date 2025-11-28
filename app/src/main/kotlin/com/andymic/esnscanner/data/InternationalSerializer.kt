package com.andymic.esnscanner.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

object InternationalSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("InternationalSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
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
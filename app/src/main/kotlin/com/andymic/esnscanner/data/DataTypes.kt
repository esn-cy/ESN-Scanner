package com.andymic.esnscanner.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalResponse(
    val name: String,
    val surname: String,
    val homeCountry: String,
    val creationDate: String,
    val lastScanDate: String,
    val profileImageURL: String,
)

@Serializable
data class InternationalResponse(
    val code: String,
    val tid: String,
    @Serializable(InternationalSerializer::class)
    @SerialName("expiration-date")
    val expirationDate: String,
    val status: String,
    @Serializable(InternationalSerializer::class)
    @SerialName("section-code")
    val sectionCode: String,
    @Serializable(InternationalSerializer::class)
    @SerialName("activation date")
    val activationDate: String? = null
)
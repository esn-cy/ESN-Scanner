package com.andymic.esnscanner.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalResponse(
    val name: String,
    val surname: String,
    val homeCountry: String,
    val lastScanDate: String,
    val profileImageURL: String,
)

@Serializable
data class InternationalResponse(
    val code: String,
    val tid: String,
    @SerialName("expiration-date")
    val expirationDate: String,
    val status: String,
    @SerialName("section-code")
    val sectionCode: String,
    @SerialName("activation date")
    val activationDate: String? = null
)
package org.esncy.esnscanner.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = LocalSerializer::class)
interface LocalResponse {
    val name: String
    val surname: String
    val dateApproved: String?
        get() = null
}

@Serializable
data class LocalPassResponse(
    override val name: String,
    override val surname: String,
    val nationality: String,
    val mobilityStatus: String,
    val datePaid: String? = null,
    override val dateApproved: String? = null,
    val lastScanDate: String? = null,
    val profileImageURL: String? = null,
) : LocalResponse

@Serializable
data class LocalGuestResponse(
    override val name: String,
    override val surname: String,
    val refererName: String,
    val refererSurname: String,
    val refererMobilityStatus: String,
    override val dateApproved: String? = null,
    val dateRedeemed: String? = null,
) : LocalResponse

@Serializable
data class InternationalResponse(
    val code: String,
    val tid: String,
    @Serializable(InternationalSerializer::class)
    @SerialName("expiration-date")
    val expirationDate: String? = null,
    val status: String,
    @Serializable(InternationalSerializer::class)
    @SerialName("section-code")
    val sectionCode: String? = null,
    @Serializable(InternationalSerializer::class)
    @SerialName("activation date")
    val activationDate: String? = null
)

@Serializable
data class DatasetResponse(
    val date: String,
    val name: String,
    val cardNumber: String,
    val pos: String,
    val host: String,
    val nationality: String,
    val payment: String,
    val amount: String
)

@Serializable
data class AddCardResponse(
    val status: String,
    val message: String
)

@Serializable
data class StatusResponse(
    val status: String,
    val message: String,
)

@Serializable
data class Section(
    var code: String,
    var name: String,
    var city: String
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Int
)

@Serializable
data class DrupalTokenPayload(
    @SerialName("permissions") val permissions: List<String> = emptyList(),
    @SerialName("name") val name: String = ""
)
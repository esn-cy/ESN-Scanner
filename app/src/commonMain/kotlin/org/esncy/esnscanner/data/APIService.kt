package org.esncy.esnscanner.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.post
import io.ktor.client.request.setBody

val datasetRegex =
    Regex("\\{\"v\":\"Date\\(\\d\\d\\d\\d,\\d\\d?,\\d\\d?\\)\",\"f\":\"(\\d\\d?/\\d\\d?/\\d\\d\\d\\d)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":[\\d.]*?,\"f\":\"([\\d.]*?)\"\\}")

class APIService(
    private val publicClient: HttpClient,
    private val privateClient: HttpClient,
    private val sectionDomain: String,
    private val spreadsheetID: String
) {
    suspend fun getLocalInfo(lookupString: String): LocalResponse? {
        try {
            val response = privateClient.post("https://$sectionDomain/api/memberships/scan") {
                setBody("{\"card\": \"$lookupString\"}")
            }
            if (response.status.value != 200)
                return null
            return response.body()
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun getInternationalInfo(cardNumber: String): InternationalResponse? {
        try {
            val body: List<InternationalResponse> =
                publicClient.get("https://esncard.org/services/1.0/card.json?code=$cardNumber")
                    .body<List<InternationalResponse>>()
            if (body.isEmpty())
                return null
            return body[0]
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun getDatasetInfo(cardNumber: String): DatasetResponse? {
        try {
            val body: String =
                publicClient.get("https://docs.google.com/spreadsheets/d/$spreadsheetID/gviz/tq?tq=SELECT A, B, C, D, E, F, G, H WHERE C = '$cardNumber'&sheet=Data&tqx=out:json")
                    .body()
            val match = datasetRegex.find(body) ?: return null
            return DatasetResponse(
                match.groupValues[1],
                match.groupValues[2],
                match.groupValues[3],
                match.groupValues[4],
                match.groupValues[5],
                match.groupValues[6],
                match.groupValues[7],
                match.groupValues[8],
            )
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun addCard(cardNumber: String): AddCardResponse? {
        try {
            val response = privateClient.post("https://esncy.org/api/memberships/add") {
                setBody("{\"card\": \"$cardNumber\"}")
            }
            if (response.status.value != 200)
                return null
            return response.body()
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun updateStatus(cardNumber: String, status: String): StatusResponse? {
        try {
            val response = privateClient.post("https://esncy.org/api/memberships/status") {
                setBody("{\"card\": \"$cardNumber\",\"status\": \"${status}\"}")
            }
            return response.body()
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun localOnlineTest(): Boolean {
        try {
            val response = privateClient.get("https://$sectionDomain/api/memberships/online") {
                timeout {
                    requestTimeoutMillis = 500
                }
            }
            return response.status.value == 200
        } catch (_: Exception) {
            return false
        }
    }

    suspend fun internationalOnlineTest(): Boolean {
        try {
            val response = publicClient.head("https://esncard.org/services/1.0/card.json") {
                timeout {
                    requestTimeoutMillis = 500
                }
            }
            return response.status.value == 200
        } catch (_: Exception) {
            return false
        }
    }

    suspend fun datasetOnlineTest(): Boolean {
        try {
            val response =
                publicClient.head("https://docs.google.com/spreadsheets/d/$spreadsheetID/gviz/tq?tq=SELECT A, B, C, D, E, F, G, H WHERE C = ''&sheet=Data&tqx=out:json") {
                    timeout {
                        requestTimeoutMillis = 500
                    }
                }
            return response.status.value == 200
        } catch (_: Exception) {
            return false
        }
    }
}
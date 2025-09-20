package com.andymic.esnscanner.data

import io.ktor.client.call.body
import io.ktor.client.request.get

interface APIService {
    suspend fun getLocalInfo(lookupString: String): LocalResponse?
    suspend fun getInternationalInfo(cardNumber: String): InternationalResponse?
    suspend fun getDatasetInfo(cardNumber: String, spreadsheetID: String): DatasetResponse?
}

val datasetRegex =
    Regex("\\{\"v\":\"Date\\(\\d\\d\\d\\d,\\d\\d?,\\d\\d?\\)\",\"f\":\"(\\d\\d?/\\d\\d?/\\d\\d\\d\\d)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":\"(.*?)\"\\},\\{\"v\":[\\d.]*?,\"f\":\"([\\d.]*?)\"\\}")

class ApiServiceImplementation(
    private val client: io.ktor.client.HttpClient
) : APIService {
    override suspend fun getLocalInfo(lookupString: String): LocalResponse? {
        return try {
            client.get("https://jsonplaceholder.typicode.com/posts").body()
        } catch (_: Exception) {
            return null
        }
    }

    override suspend fun getInternationalInfo(cardNumber: String): InternationalResponse? {
        try {
            val body: List<InternationalResponse> = client.get("https://esncard.org/services/1.0/card.json?code=$cardNumber").body<List<InternationalResponse>>()
            if (body.isEmpty())
                return null
            return body[0]
        } catch (_: Exception) {
            return null
        }
    }

    override suspend fun getDatasetInfo(
        cardNumber: String,
        spreadsheetID: String
    ): DatasetResponse? {
        try {
            val body: String =
                client.get("https://docs.google.com/spreadsheets/d/$spreadsheetID/gviz/tq?tq=SELECT A, B, C, D, E, F, G, H WHERE C = '$cardNumber'&sheet=Data&tqx=out:json")
                    .body()
            val match = datasetRegex.find(body)
            if (match == null)
                return null
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
}
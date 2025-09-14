package com.andymic.esnscanner.data

import io.ktor.client.call.body
import io.ktor.client.request.get

interface APIService {
    suspend fun getLocalInfo(lookupString: String): LocalResponse?
    suspend fun getInternationalInfo(cardNumber: String): InternationalResponse?
}

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
}
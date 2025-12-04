package com.andymic.esnscanner.data

import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.head

interface TestService {
    suspend fun local(): Boolean
    suspend fun international(): Boolean
    suspend fun dataset(): Boolean
}

class TestServiceImplementation(
    private val client: io.ktor.client.HttpClient,
    private val sectionDomain: String,
    private val spreadsheetID: String
) : TestService {
    override suspend fun local(): Boolean {
        try {
            val response = client.get("https://$sectionDomain/api/esncard/scan") {
                timeout {
                    requestTimeoutMillis = 500
                }
            }
            return response.status.value == 200
        } catch (_: Exception) {
            return false
        }
    }

    override suspend fun international(): Boolean {
        try {
            val response = client.head("https://esncard.org/services/1.0/card.json") {
                timeout {
                    requestTimeoutMillis = 500
                }
            }
            return response.status.value == 200
        } catch (_: Exception) {
            return false
        }
    }

    override suspend fun dataset(): Boolean {
        try {
            val response =
                client.head("https://docs.google.com/spreadsheets/d/$spreadsheetID/gviz/tq?tq=SELECT A, B, C, D, E, F, G, H WHERE C = ''&sheet=Data&tqx=out:json") {
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
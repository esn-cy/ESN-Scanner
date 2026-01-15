package org.esncy.esnscanner.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.esncy.esnscanner.models.TokenViewModel

expect val engine: HttpClientEngineFactory<*>

class KtorClients(
    tokenViewModel: TokenViewModel
) {
    val publicClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    val privateClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = tokenViewModel.dataFlow.value.access
                    val refreshToken = tokenViewModel.dataFlow.value.refresh
                    if (accessToken != "" && refreshToken != "") {
                        BearerTokens(accessToken, refreshToken)
                    } else {
                        null
                    }
                }

                refreshTokens {
                    try {
                        val response: TokenResponse = client.submitForm(
                            url = "https://${tokenViewModel.sectionData.localSectionDomain}/oauth/token",
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("refresh_token", oldTokens?.refreshToken ?: "")
                                append("client_id", "45534e20-5363-616e-6572-204f41757468")
                            }
                        ) { markAsRefreshTokenRequest() }.body()

                        val payload = tokenViewModel.decodePayload(response.accessToken)
                        if (payload != null) {
                            tokenViewModel.updateTokens(
                                response.accessToken,
                                response.refreshToken,
                                payload.permissions.toSet(),
                                payload.name
                            )
                        } else {
                            tokenViewModel.updateTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                        }
                        BearerTokens(response.accessToken, response.refreshToken)
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }
        }
    }
}
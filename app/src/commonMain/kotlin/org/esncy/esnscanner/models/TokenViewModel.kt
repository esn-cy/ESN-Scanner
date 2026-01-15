package org.esncy.esnscanner.models

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.esncy.esnscanner.data.DrupalTokenPayload
import org.esncy.esnscanner.data.KtorClients
import org.esncy.esnscanner.data.TokenResponse
import org.esncy.esnscanner.dataStore
import org.kotlincrypto.hash.sha2.SHA256
import org.kotlincrypto.random.CryptoRand
import kotlin.io.encoding.Base64

data class Token(
    val access: String = "",
    val refresh: String = "",
    val permissions: List<String> = emptyList(),
    val name: String = ""
)

sealed interface TokenUIState {
    data object Loading : TokenUIState
    data class Success(val result: Token) : TokenUIState
    data class Error(val error: Exception? = null) : TokenUIState
}

sealed interface AuthUIState {
    data object Idle : AuthUIState
    data class InProgress(val verifier: String) : AuthUIState
    data object Authenticating : AuthUIState
    data class Error(val error: Exception? = null) : AuthUIState
}

class TokenViewModel(
    private val sectionDataFlow: StateFlow<SectionData>
) : ViewModel() {
    val sectionData: SectionData
        get() = sectionDataFlow.value

    private val KEY_ACCESS_TOKEN = stringPreferencesKey("accessToken")
    private val KEY_REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    private val KEY_PERMISSIONS = stringSetPreferencesKey("permissions")
    private val KEY_NAME_OF_USER = stringPreferencesKey("nameOfUser")


    val state: StateFlow<TokenUIState> = dataStore.data
        .map { prefs ->
            Token(
                access = prefs[KEY_ACCESS_TOKEN] ?: "",
                refresh = prefs[KEY_REFRESH_TOKEN] ?: "",
                permissions = prefs[KEY_PERMISSIONS]?.toList() ?: emptyList(),
                name = prefs[KEY_NAME_OF_USER] ?: ""
            )
        }
        .map { TokenUIState.Success(it) as TokenUIState }
        .catch { emit(TokenUIState.Error(it as Exception?)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TokenUIState.Loading
        )

    private val _authState = MutableStateFlow<AuthUIState>(AuthUIState.Idle)
    val authState = _authState.asStateFlow()

    val dataFlow: StateFlow<Token> = state
        .map { (it as? TokenUIState.Success)?.result ?: Token() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Token()
        )

    fun startLogin(): String {
        val verifier = Base64.UrlSafe
            .encode(CryptoRand.nextBytes(ByteArray(128)))
            .slice(0..127)

        val challenge = Base64.UrlSafe
            .encode(SHA256().digest(verifier.toByteArray()))
            .replace("=", "")

        _authState.value = AuthUIState.InProgress(verifier)

        return "https://${sectionData.localSectionDomain}/oauth/authorize?" +
                "response_type=code&" +
                "client_id=45534e20-5363-616e-6572-204f41757468&" +
                "scope=esn_scanner&" +
                "code_challenge=$challenge&" +
                "code_challenge_method=S256&" +
                "redirect_uri=org.esncy.esnscanner://callback"
    }

    fun handleCallback(code: String) {
        val currentState = _authState.value
        if (currentState !is AuthUIState.InProgress) return

        val verifier = currentState.verifier
        _authState.value = AuthUIState.Authenticating

        val client = KtorClients(this).privateClient

        viewModelScope.launch {
            try {
                val response: TokenResponse = client.submitForm(
                    url = "https://${sectionData.localSectionDomain}/oauth/token",
                    formParameters = Parameters.build {
                        append("grant_type", "authorization_code")
                        append("client_id", "45534e20-5363-616e-6572-204f41757468")
                        append("code", code)
                        append("code_verifier", verifier)
                        append("redirect_uri", "org.esncy.esnscanner://callback")
                    }
                ).body()

                val payload = decodePayload(response.accessToken)
                if (payload != null) {
                    updateTokens(
                        response.accessToken,
                        response.refreshToken,
                        payload.permissions.toSet(),
                        payload.name
                    )

                }

                _authState.value = AuthUIState.Idle
            } catch (_: Exception) {
                _authState.value = AuthUIState.Idle
            }
        }
    }

    fun logout() {
        updateTokens()
    }

    fun updateTokens(
        accessToken: String = "",
        refreshToken: String = "",
        permissions: Set<String> = emptySet(),
        name: String = ""
    ) {
        try {
            viewModelScope.launch {
                dataStore.edit { preferences ->
                    accessToken.let { preferences[KEY_ACCESS_TOKEN] = it }
                    refreshToken.let { preferences[KEY_REFRESH_TOKEN] = it }
                    permissions.let { preferences[KEY_PERMISSIONS] = it }
                    name.let { preferences[KEY_NAME_OF_USER] = it }
                }
            }
        } catch (_: Exception) {
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun decodePayload(token: String): DrupalTokenPayload? {
        return try {
            val payloadPart = token.split(".")[1]
                .replace('-', '+').replace('_', '/')
            val decoded =
                Base64.withPadding(Base64.PaddingOption.ABSENT).decode(payloadPart).decodeToString()
            json.decodeFromString<DrupalTokenPayload>(decoded)
        } catch (_: Exception) {
            null
        }
    }
}
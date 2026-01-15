package org.esncy.esnscanner.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.esncy.esnscanner.models.TokenUIState
import org.esncy.esnscanner.models.TokenViewModel
import org.esncy.esnscanner.ui.components.AuthLauncher

@Composable
fun AccountControls(
    modifier: Modifier,
    viewModel: TokenViewModel,
    authLauncher: AuthLauncher
) {
    val uiState by viewModel.state.collectAsState()

    Column(
        modifier = modifier
    ) {
        if ((uiState as? TokenUIState.Success)?.result?.access != "") {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Text(
                    text = "Logged in as: " + (uiState as TokenUIState.Success).result.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
        Button(
            onClick = {
                when (uiState) {
                    TokenUIState.Loading -> {}
                    is TokenUIState.Success -> {
                        if ((uiState as TokenUIState.Success).result.access == "") {
                            val url = viewModel.startLogin()
                            authLauncher.launchBrowser(url)
                        } else
                            viewModel.logout()
                    }

                    is TokenUIState.Error -> {}
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp, 16.dp, 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            val buttonText = when (uiState) {
                TokenUIState.Loading -> "Loading..."
                is TokenUIState.Success -> {
                    if ((uiState as TokenUIState.Success).result.access == "")
                        "Login"
                    else
                        "Logout"
                }

                is TokenUIState.Error -> "ERROR"
            }
            Text(
                text = buttonText,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}
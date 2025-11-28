package com.andymic.esnscanner.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andymic.esnscanner.models.SectionDataUIState
import com.andymic.esnscanner.models.SectionDataViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectionDataFields(
    modifier: Modifier = Modifier,
    onSave: (String, String, String, String) -> Unit,
    viewModel: SectionDataViewModel
) {
    val uiState = viewModel.state.collectAsState()

    val sectionName = rememberTextFieldState("")
    val sectionCode = rememberTextFieldState("")
    val sectionDomain = rememberTextFieldState("")
    val spreadsheetID = rememberTextFieldState("")

    var isDataLocked by remember { mutableStateOf(true) }

    LaunchedEffect(uiState) {
        if (uiState.value is SectionDataUIState.Success) {
            if (isDataLocked) {
                sectionName.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.localSectionName)
                sectionCode.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.localSectionCode)
                sectionDomain.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.localSectionDomain)
                spreadsheetID.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.spreadsheetID)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier.fillMaxWidth(), Alignment.CenterEnd) {
            if (uiState.value is SectionDataUIState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(
                onClick = {
                    if (!isDataLocked) {
                        onSave(
                            sectionName.text.toString(),
                            sectionCode.text.toString(),
                            sectionDomain.text.toString(),
                            spreadsheetID.text.toString()
                        )
                    }
                    isDataLocked = !isDataLocked
                }
            ) {
                if (isDataLocked)
                    Icon(Icons.Default.Lock, "Data Locked")
                else
                    Icon(Icons.Default.LockOpen, "Data Unlocked")
            }
        }
        OutlinedTextField(
            state = sectionName,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDataLocked,
            label = { Text("Section Name") }
        )
        OutlinedTextField(
            state = sectionCode,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDataLocked,
            label = { Text("Section Code") }
        )
        OutlinedTextField(
            state = sectionDomain,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDataLocked,
            label = { Text("Section Domain") }
        )
        OutlinedTextField(
            state = spreadsheetID,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDataLocked,
            label = { Text("Spreadsheet ID") }
        )

        if (uiState.value is SectionDataUIState.Error) {
            Text(
                text = "Error saving: ${(uiState.value as SectionDataUIState.Error).error?.message}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
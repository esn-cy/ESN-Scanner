package org.esncy.esnscanner.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.models.TokenViewModel
import org.esncy.esnscanner.ui.components.AuthLauncher
import org.esncy.esnscanner.ui.components.settings.AccountControls
import org.esncy.esnscanner.ui.components.settings.SectionDataFields

@Composable
fun SettingsScreen(
    sectionDataViewModel: SectionDataViewModel,
    tokenViewModel: TokenViewModel,
    authLauncher: AuthLauncher
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionDataFields(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onSave = { localSectionName,
                       localSectionCode,
                       localSectionDomain,
                       spreadsheetID ->

                sectionDataViewModel.updateData(
                    localSectionName,
                    localSectionCode,
                    localSectionDomain,
                    spreadsheetID
                )
            },
            viewModel = sectionDataViewModel,
        )
        Spacer(Modifier.weight(1f))
        AccountControls(
            modifier = Modifier,
            viewModel = tokenViewModel,
            authLauncher = authLauncher
        )
    }
}
package week11.st910491.finalproject.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import week11.st910491.finalproject.data.UserPreferencesRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    userPreferencesRepository: UserPreferencesRepository
) {
    val scope = rememberCoroutineScope()
    val highContrastEnabled by userPreferencesRepository.isHighContrast.collectAsState(initial = false)
    val largeTextEnabled by userPreferencesRepository.isLargeText.collectAsState(initial = false)
    val oneHandModeEnabled by userPreferencesRepository.isOneHanded.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = { navController.navigateUp() }) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Accessibility",
                style = MaterialTheme.typography.titleMedium
            )

            SettingToggleRow(
                title = "High-contrast mode",
                description = "Use stronger colours and contrast for better visibility.",
                checked = highContrastEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        userPreferencesRepository.setHighContrast(enabled)
                    }
                }
            )

            SettingToggleRow(
                title = "Large text",
                description = "Increase text size across the app for easier reading.",
                checked = largeTextEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        userPreferencesRepository.setLargeText(enabled)
                    }
                }
            )

            SettingToggleRow(
                title = "One-hand mode",
                description = "Keep important actions near the bottom of the screen.",
                checked = oneHandModeEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        userPreferencesRepository.setOneHanded(enabled)
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

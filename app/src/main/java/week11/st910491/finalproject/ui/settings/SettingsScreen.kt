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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    // Step-2: these are local only; later you can connect to DataStore.
    var highContrastEnabled by rememberSaveable { mutableStateOf(false) }
    var largeTextEnabled by rememberSaveable { mutableStateOf(false) }
    var oneHandModeEnabled by rememberSaveable { mutableStateOf(false) }

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
                onCheckedChange = { highContrastEnabled = it }
            )

            SettingToggleRow(
                title = "Large text",
                description = "Increase text size across the app for easier reading.",
                checked = largeTextEnabled,
                onCheckedChange = { largeTextEnabled = it }
            )

            SettingToggleRow(
                title = "One-hand mode",
                description = "Keep important actions near the bottom of the screen.",
                checked = oneHandModeEnabled,
                onCheckedChange = { oneHandModeEnabled = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Note: In the final step these toggles will be persisted " +
                        "using DataStore and applied to the app theme.",
                style = MaterialTheme.typography.bodyMedium
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

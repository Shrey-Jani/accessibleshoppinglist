package week11.st910491.finalproject.ui.settings

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
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
    // Observe values from DataStore
    val highContrastEnabled by userPreferencesRepository.isHighContrast.collectAsState(initial = false)
    val largeTextEnabled by userPreferencesRepository.isLargeText.collectAsState(initial = false)
    val oneHandModeEnabled by userPreferencesRepository.isOneHanded.collectAsState(initial = false)

    // For Haptic Feedback
    val view = LocalView.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Accessibility Preferences",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 1. High Contrast Toggle
            SettingToggleRow(
                title = "High-contrast mode",
                description = "Use stronger borders and colors for better visibility.",
                icon = Icons.Default.Contrast,
                checked = highContrastEnabled,
                onCheckedChange = { enabled ->
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    scope.launch {
                        userPreferencesRepository.setHighContrast(enabled)
                    }
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 2. Large Text Toggle
            SettingToggleRow(
                title = "Large text",
                description = "Increase text size across the app for easier reading.",
                icon = Icons.Default.FormatSize,
                checked = largeTextEnabled,
                onCheckedChange = { enabled ->
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    scope.launch {
                        userPreferencesRepository.setLargeText(enabled)
                    }
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 3. One-Handed Mode Toggle
            SettingToggleRow(
                title = "One-hand mode",
                description = "Move main actions (like Add and Settings) to the bottom of the screen.",
                icon = Icons.Default.Smartphone,
                checked = oneHandModeEnabled,
                onCheckedChange = { enabled ->
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    scope.launch {
                        userPreferencesRepository.setOneHanded(enabled)
                    }
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // 4. Help Section
            Text(
                text = "Help & Guide",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            HelpRow(
                icon = Icons.Default.Mic,
                title = "Voice Commands",
                description = "Tap the microphone on the 'Add Item' screen and say the item name (e.g., 'Milk')."
            )
            Spacer(modifier = Modifier.height(12.dp))
            HelpRow(
                icon = Icons.Default.VolumeUp,
                title = "Text-to-Speech",
                description = "Tap 'Read List Aloud' to hear your shopping list spoken to you."
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // ACCESSIBILITY IMPROVISATION:
    // We use Modifier.toggleable on the Row itself.
    // This makes the entire row clickable, creating a massive touch target.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp) // Generous height for touch
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch // Tells TalkBack this whole row behaves like a switch
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp).size(24.dp)
        )
    
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // The Switch itself doesn't need to be clickable since the Row handles it.
        // We set onCheckedChange to null here to prevent double-click handling issues.
        Switch(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun HelpRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 2.dp, end = 16.dp).size(24.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
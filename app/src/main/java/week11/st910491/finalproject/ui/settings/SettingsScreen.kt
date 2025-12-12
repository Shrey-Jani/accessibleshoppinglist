package week11.st910491.finalproject.ui.settings

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
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
    val view = LocalView.current

    // --- SHARED STYLING CONSTANTS ---
    val primaryColor = Color(0xFF3F51B5) // Deep Indigo
    val navyTextColor = Color(0xFF1A237E) // Dark Navy
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE3F2FD), Color(0xFFF3E5F5))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Settings",
                            fontWeight = FontWeight.Bold,
                            color = navyTextColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                                tint = primaryColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. ACCESSIBILITY SECTION
                SectionHeader("Accessibility Preferences", navyTextColor)

                // Group settings in a card for professional look
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    SettingToggleRow(
                        title = "High-contrast mode",
                        description = "Use stronger borders and colors.",
                        icon = Icons.Default.Contrast,
                        checked = highContrastEnabled,
                        primaryColor = primaryColor,
                        onCheckedChange = { enabled ->
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            scope.launch { userPreferencesRepository.setHighContrast(enabled) }
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    SettingToggleRow(
                        title = "Large text",
                        description = "Increase text size across the app.",
                        icon = Icons.Default.FormatSize,
                        checked = largeTextEnabled,
                        primaryColor = primaryColor,
                        onCheckedChange = { enabled ->
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            scope.launch { userPreferencesRepository.setLargeText(enabled) }
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    SettingToggleRow(
                        title = "One-hand mode",
                        description = "Move main actions to bottom.",
                        icon = Icons.Default.Smartphone,
                        checked = oneHandModeEnabled,
                        primaryColor = primaryColor,
                        onCheckedChange = { enabled ->
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            scope.launch { userPreferencesRepository.setOneHanded(enabled) }
                        }
                    )
                }

                // 2. HELP SECTION
                SectionHeader("Help & Guide", navyTextColor)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HelpRow(
                        icon = Icons.Default.Mic,
                        title = "Voice Commands",
                        description = "Tap the microphone on the 'Add Item' screen and say the item name.",
                        tint = primaryColor
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    HelpRow(
                        icon = Icons.Default.VolumeUp,
                        title = "Text-to-Speech",
                        description = "Tap 'Read List Aloud' to hear your shopping list spoken to you.",
                        tint = primaryColor
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = color,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    primaryColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with subtle background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(primaryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.4f)
            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun HelpRow(
    icon: ImageVector,
    title: String,
    description: String,
    tint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .padding(top = 2.dp, end = 16.dp)
                .size(24.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
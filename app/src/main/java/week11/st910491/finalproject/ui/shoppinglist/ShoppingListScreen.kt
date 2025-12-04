package week11.st910491.finalproject.ui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect // Import needed for TTS cleanup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember // Import needed for TTS state
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Import needed for TTS context
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import week11.st910491.finalproject.navigation.Routes
import week11.st910491.finalproject.ui.components.AccessibleButton
import week11.st910491.finalproject.ui.components.AccessibleCard
// Make sure to import your new TTS Manager
import week11.st910491.finalproject.ui.voice.TextToSpeechManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    isOneHanded: Boolean = false,
    isHighContrast: Boolean = false,
    isLargeText: Boolean = false,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val items = state.data.orEmpty()

    // NEW FEATURE: Initialize Text-to-Speech Manager
    val context = LocalContext.current
    val ttsManager = remember { TextToSpeechManager(context) }

    // Cleanup TTS when screen is destroyed
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping list") },
                actions = {
                    if (!isOneHanded) {
                        TextButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Text("Settings")
                        }
                        TextButton(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.SHOPPING_LIST) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Logout")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isOneHanded) {
                // One-handed mode: Controls at bottom
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

                    // NEW FEATURE: Read Aloud Button in Bottom Bar (One-Handed)
                    AccessibleButton(
                        text = "Read List Aloud",
                        onClick = {
                            val textToRead = if (items.isEmpty()) {
                                "Your shopping list is empty."
                            } else {
                                "You have ${items.size} items. " +
                                        items.joinToString(", ") { "${it.quantity} ${it.name}" }
                            }
                            ttsManager.speak(textToRead)
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        isHighContrast = isHighContrast,
                        isLargeText = isLargeText,
                        contentDesc = "Read shopping list out loud"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccessibleButton(
                            text = "Settings",
                            onClick = { navController.navigate(Routes.SETTINGS) },
                            modifier = Modifier.weight(1f),
                            isHighContrast = isHighContrast,
                            isLargeText = isLargeText,
                            contentDesc = "Open settings"
                        )
                        AccessibleButton(
                            text = "Logout",
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.SHOPPING_LIST) { inclusive = true }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            isHighContrast = isHighContrast,
                            isLargeText = isLargeText,
                            contentDesc = "Logout from account"
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_EDIT_ITEM) }
            ) {
                Text("Add item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // NEW FEATURE: Read Aloud Button (Standard Mode - Top of List)
            if (!isOneHanded) {
                AccessibleButton(
                    text = "Read List Aloud",
                    onClick = {
                        val textToRead = if (items.isEmpty()) {
                            "Your shopping list is empty."
                        } else {
                            "You have ${items.size} items. " +
                                    items.joinToString(", ") { "${it.quantity} ${it.name}" }
                        }
                        ttsManager.speak(textToRead)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    isHighContrast = isHighContrast,
                    isLargeText = isLargeText,
                    contentDesc = "Read shopping list out loud"
                )
            }

            if (state.isLoading) {
                Text("Loading...")
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!state.isLoading && items.isEmpty()) {
                week11.st910491.finalproject.ui.components.EmptyState(
                    message = "Your list is empty.\nTap \"Add item\" or use Voice Commands to start.",
                    isHighContrast = isHighContrast
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        AccessibleCard(
                            isHighContrast = isHighContrast,
                            // Improve semantic description for TalkBack
                            contentDesc = "Item ${item.name}, quantity ${item.quantity}, category ${item.category}"
                        ) {
                            ShoppingListItemCard(
                                item = item,
                                onTogglePurchased = { viewModel.togglePurchased(item) },
                                onDelete = { viewModel.deleteItem(item) },
                                onClick = {
                                    navController.navigate("${Routes.ADD_EDIT_ITEM}/${item.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
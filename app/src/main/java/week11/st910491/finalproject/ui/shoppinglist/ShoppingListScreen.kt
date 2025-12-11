package week11.st910491.finalproject.ui.shoppinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import week11.st910491.finalproject.navigation.Routes
import week11.st910491.finalproject.ui.components.AccessibleButton
import week11.st910491.finalproject.ui.components.AccessibleCard
import week11.st910491.finalproject.ui.voice.TextToSpeechManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    isOneHanded: Boolean = false,
    isHighContrast: Boolean = false,
    isLargeText: Boolean = false,
    viewModel: ShoppingListViewModel = viewModel(),
    onLogout: () -> Unit // New callback for logout logic
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

    // --- FINISH SHOPPING DIALOG ---
    var showFinishDialog by remember { mutableStateOf(false) }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Shopping?") },
            text = { Text("This will save your trip history and clear your current list. Are you sure?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.finishShopping()
                        showFinishDialog = false
                    }
                ) {
                    Text("Yes, Finish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping list") },
                actions = {
                    // NEW: Analytics Button
                    IconButton(onClick = { navController.navigate(Routes.ANALYTICS) }) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "View shopping analytics"
                        )
                    }
                    TextButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Text("Settings")
                    }
                    TextButton(
                        onClick = onLogout // Use the callback
                    ) {
                        Text("Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // FINISH SHOPPING BUTTON
                if (items.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { showFinishDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.semantics {
                            // Make the purpose very clear for screen readers
                            this.contentDescription = "Finish current shopping trip and clear the list"
                        },
                        icon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null
                            )
                        },
                        text = { Text("Finish Shopping") }
                    )
                }

                // ADD ITEM BUTTON
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_EDIT_ITEM) },
                    modifier = Modifier.semantics {
                        this.contentDescription = "Add a new shopping list item"
                    },
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    text = { Text("Add Item") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Read Aloud Button (Always at top of list)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 8.dp),
                isHighContrast = isHighContrast,
                isLargeText = isLargeText,
                contentDesc = "Read shopping list out loud"
            )

            if (state.isLoading) {
                Text("Loading...", modifier = Modifier.padding(top = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
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
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        AccessibleCard(
                            isHighContrast = isHighContrast,
                            contentDesc = "Item ${item.name}, quantity ${item.quantity}, category ${item.category}",
                            modifier = Modifier.animateItem()
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
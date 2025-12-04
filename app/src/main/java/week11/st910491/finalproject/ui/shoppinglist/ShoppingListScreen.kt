package week11.st910491.finalproject.ui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.navigation.Routes
import week11.st910491.finalproject.ui.common.AccessibleButton
import week11.st910491.finalproject.ui.common.AccessibleCard
import week11.st910491.finalproject.ui.voice.TextToSpeechManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    isOneHanded: Boolean = false,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val items = state.data.orEmpty()

    // --- TTS Setup ---
    val context = LocalContext.current
    val ttsManager = remember { TextToSpeechManager(context) }
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping list") },
                actions = {
                    if (!isOneHanded) {
                        ShoppingListActions(navController)
                    }
                }
            )
        },
        bottomBar = {
            if (isOneHanded) {
                BottomAppBar(
                    actions = { ShoppingListActions(navController) },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { navController.navigate(Routes.ADD_EDIT_ITEM) },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add item")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isOneHanded) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_EDIT_ITEM) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add item")
                }
            }
        }
    ) { paddingValues ->
        // FIX: Using a single LazyColumn for EVERYTHING ensures perfect scrolling
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 1. HEADER: Read Aloud Button
            item {
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
                    }
                )
            }

            // 2. STATES: Loading, Error, or Empty
            if (state.isLoading) {
                item {
                    Text("Loading...")
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            }
            if (state.errorMessage != null) {
                item {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (!state.isLoading && items.isEmpty()) {
                item {
                    Text(
                        text = "Your list is empty. Tap \"+\" to start.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 3. CONTENT: Shopping Items
            // Note: We use 'items' (plural) here, which works inside LazyColumn
            if (!state.isLoading && items.isNotEmpty()) {
                items(items) { item ->
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

            // Add extra spacer at bottom so FAB doesn't cover last item
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ShoppingListActions(navController: NavHostController) {
    TextButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
        Icon(Icons.Default.Settings, contentDescription = null)
        Spacer(modifier = Modifier.padding(4.dp))
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

@Composable
private fun ShoppingListItemCard(
    item: ShoppingItem,
    onTogglePurchased: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    AccessibleCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        // ... (This part of the card UI remains exactly the same) ...
        AccessibleCard(
            item = item,
            onTogglePurchased = onTogglePurchased,
            onDelete = onDelete
        )
    }
}

// Helper to keep the file clean - paste this at the bottom if not using the inline version above
@Composable
fun AccessibleCard(
    item: ShoppingItem,
    onTogglePurchased: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = item.name, style = MaterialTheme.typography.titleMedium)
        Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
        if (item.category.isNotBlank()) {
            Text(text = "Category: ${item.category}", style = MaterialTheme.typography.bodyMedium)
        }
        if (item.notes.isNotBlank()) {
            Text(text = item.notes, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = if (item.isPurchased) "Purchased" else "Not purchased",
            style = MaterialTheme.typography.bodyMedium,
            color = if (item.isPurchased) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onTogglePurchased, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                Text(if (item.isPurchased) "Unmark" else "Mark Done")
            }
            OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                Text("Delete")
            }
        }
    }
}
package week11.st910491.finalproject.ui.shoppinglist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
//import week11.st910491.finalproject.data.model.ShoppingItem
import week11.st910491.finalproject.navigation.Routes
import week11.st910491.finalproject.ui.components.EmptyState
import week11.st910491.finalproject.ui.voice.TextToSpeechManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    isOneHanded: Boolean = false,
    isHighContrast: Boolean = false,
    isLargeText: Boolean = false,
    viewModel: ShoppingListViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val items = state.data.orEmpty()

    val context = LocalContext.current
    val ttsManager = remember { TextToSpeechManager(context) }

    // --- SHARED STYLING CONSTANTS ---
    val primaryColor = Color(0xFF3F51B5) // Deep Indigo
    // Soft gradient background (Light Blue -> Light Purple/White)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE3F2FD), Color(0xFFF3E5F5))
    )

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
                ) { Text("Yes, Finish") }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Shopping list",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1A237E) // Dark Navy Title
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Make TopAppBar transparent to show gradient
                ),
                actions = {
                    // Analytics Icon
                    IconButton(onClick = { navController.navigate(Routes.ANALYTICS) }) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "View shopping analytics",
                            tint = primaryColor
                        )
                    }
                    // Settings Icon (replaces text)
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = primaryColor
                        )
                    }
                    // Logout Icon (replaces text)
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.PowerSettingsNew,
                            contentDescription = "Logout",
                            tint = primaryColor
                        )
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
                        containerColor = Color(0xFF4CAF50), // Green color
                        contentColor = Color.White,
                        modifier = Modifier.semantics {
                            this.contentDescription = "Finish current shopping trip and clear the list"
                        },
                        icon = { Icon(Icons.Default.Check, contentDescription = null) },
                        text = { Text("Finish Shopping", fontWeight = FontWeight.Bold) }
                    )
                }

                // ADD ITEM BUTTON
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_EDIT_ITEM) },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    modifier = Modifier.semantics {
                        this.contentDescription = "Add a new shopping list item"
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Item", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush) // Apply Gradient Background
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // --- READ ALOUD BUTTON ---
                Button(
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
                        .padding(vertical = 16.dp)
                        .height(50.dp), // Taller button
                    shape = RoundedCornerShape(25.dp), // More rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read List Aloud", fontWeight = FontWeight.Bold)
                }

                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (!state.isLoading && items.isEmpty()) {
                    EmptyState(
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
                            // --- NEW ITEM CARD DESIGN ---
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
                                    .animateItem()
                                    .clickable { navController.navigate("${Routes.ADD_EDIT_ITEM}/${item.id}") },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Checkbox
                                        Checkbox(
                                            checked = item.isPurchased,
                                            onCheckedChange = { viewModel.togglePurchased(item) },
                                            colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            // Item Name
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            // Quantity
                                            Text(
                                                text = "Quantity: ${item.quantity}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    // Delete Button
                                    IconButton(onClick = { viewModel.deleteItem(item) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete ${item.name}",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package week11.st910491.finalproject.ui.addedit

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.ui.common.CategoryHelper
import week11.st910491.finalproject.ui.shoppinglist.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    navController: NavHostController,
    itemId: String?, // null = Add, not null = Edit
    viewModel: ShoppingListViewModel = viewModel()
) {
    val context = LocalContext.current

    // Form state
    var nameInput by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("1") }
    var categoryInput by rememberSaveable { mutableStateOf("") }
    var notesInput by rememberSaveable { mutableStateOf("") }

    // Load existing item if in "Edit" mode
    LaunchedEffect(itemId) {
        if (itemId != null) {
            // In a real app, you might fetch this from the ViewModel or DB
            // For now, we assume the ViewModel has the list loaded or we pass the item
            // This is a placeholder for loading logic
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId == null) "Add item" else "Edit item") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val qty = quantityInput.toIntOrNull() ?: 1
                    val baseItem = ShoppingItem(
                        id = itemId ?: "", // If ID is null, ViewModel/Repository should generate it
                        name = nameInput.trim(),
                        quantity = qty,
                        category = categoryInput.trim(),
                        notes = notesInput.trim()
                    )

                    if (itemId == null) {
                        viewModel.addItem(baseItem)
                    } else {
                        viewModel.updateItem(baseItem)
                    }
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Save item"
                )
            }
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
                text = "Create or edit a shopping item.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Item name input
            // Removed the Row since the Mic button is gone
            OutlinedTextField(
                value = nameInput,
                onValueChange = { newName ->
                    nameInput = newName

                    // --- SMART CATEGORIZATION FEATURE ---
                    // Automatically suggest category based on item name
                    val suggested = CategoryHelper.getCategoryFor(newName)
                    if (suggested != null) {
                        categoryInput = suggested
                    }
                },
                label = { Text("Item Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantityInput,
                onValueChange = { quantityInput = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoryInput,
                onValueChange = { categoryInput = it },
                label = { Text("Category (Auto-filled)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notesInput,
                onValueChange = { notesInput = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val qty = quantityInput.toIntOrNull() ?: 1
                    val baseItem = ShoppingItem(
                        id = itemId ?: "",
                        name = nameInput.trim(),
                        quantity = qty,
                        category = categoryInput.trim(),
                        notes = notesInput.trim()
                    )

                    if (itemId == null) {
                        viewModel.addItem(baseItem)
                    } else {
                        viewModel.updateItem(baseItem)
                    }
                    navController.popBackStack()
                },
                enabled = nameInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp) // Accessible touch target height
            ) {
                Text("Save item")
            }
        }
    }
}
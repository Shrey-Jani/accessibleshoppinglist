package week11.st910491.finalproject.ui.shoppinglist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.navigation.Routes

@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var nameInput by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("1") }
    var categoryInput by rememberSaveable { mutableStateOf("") }
    var notesInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header + Logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Shopping List")
            Button(
                onClick = {
                    // Direct logout + navigation from this screen
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SHOPPING_LIST) { inclusive = true }
                    }
                }
            ) {
                Text(text = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create item form
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Item name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantityInput,
            onValueChange = { quantityInput = it },
            label = { Text("Quantity (default 1)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoryInput,
            onValueChange = { categoryInput = it },
            label = { Text("Category (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notesInput,
            onValueChange = { notesInput = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        )

        Button(
            onClick = {
                val qty = quantityInput.toIntOrNull() ?: 1
                viewModel.addItem(
                    name = nameInput.trim(),
                    quantity = qty,
                    category = categoryInput.trim(),
                    notes = notesInput.trim()
                )
                // clear inputs
                nameInput = ""
                quantityInput = "1"
                categoryInput = ""
                notesInput = ""
            },
            enabled = nameInput.isNotBlank(),
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.End)
        ) {
            Text(text = "Add item")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // Error + loading
        if (state.isLoading) {
            Text(text = "Loading...")
        }
        if (state.errorMessage != null) {
            Text(text = "Error: ${state.errorMessage}")
        }

        // Items list
        val items = state.data.orEmpty()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                ShoppingListItemRow(
                    item = item,
                    onTogglePurchased = { viewModel.togglePurchased(item) },
                    onDelete = { viewModel.deleteItem(item) }
                )
            }
        }
    }
}

@Composable
private fun ShoppingListItemRow(
    item: ShoppingItem,
    onTogglePurchased: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = item.name)
        Text(text = "Quantity: ${item.quantity}")
        if (item.category.isNotBlank()) {
            Text(text = "Category: ${item.category}")
        }
        if (item.notes.isNotBlank()) {
            Text(text = "Notes: ${item.notes}")
        }
        Text(text = if (item.isPurchased) "Status: Purchased" else "Status: Not purchased")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onTogglePurchased) {
                Text(text = if (item.isPurchased) "Unmark purchased" else "Mark purchased")
            }
            TextButton(onClick = onDelete) {
                Text(text = "Delete")
            }
        }
    }
}

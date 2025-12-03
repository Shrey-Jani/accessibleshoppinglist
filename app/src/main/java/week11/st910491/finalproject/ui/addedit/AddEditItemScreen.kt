package week11.st910491.finalproject.ui.addedit

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.ui.shoppinglist.ShoppingListViewModel
import week11.st910491.finalproject.ui.voice.VoiceRecognizerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    navController: NavHostController,
    itemId: String?,                                // null = Add, not null = Edit
    viewModel: ShoppingListViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity

    // form state
    var nameInput by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("1") }
    var categoryInput by rememberSaveable { mutableStateOf("") }
    var notesInput by rememberSaveable { mutableStateOf("") }

    // voice state
    var isListening by remember { mutableStateOf(false) }
    var voiceError by remember { mutableStateOf<String?>(null) }

    val voiceManager = remember {
        VoiceRecognizerManager(
            activity = activity,
            onResult = { resultText ->
                nameInput = resultText
                voiceError = null
            },
            onError = { error ->
                voiceError = error
            },
            onListeningStateChanged = { listening ->
                isListening = listening
            }
        )
    }

    // clean up recognizer
    DisposableEffect(Unit) {
        onDispose { voiceManager.destroy() }
    }

    // microphone permission
    val audioPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                voiceManager.startListening()
            } else {
                voiceError = "Microphone permission denied"
            }
        }

    // prefill fields when editing
    LaunchedEffect(itemId) {
        if (!itemId.isNullOrEmpty()) {
            val existing = viewModel.getItemById(itemId)
            existing?.let { item ->
                nameInput = item.name
                quantityInput = item.quantity.toString()
                categoryInput = item.category
                notesInput = item.notes
            }
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

            // Item name + mic
            Text(text = "Item name", style = MaterialTheme.typography.labelLarge)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardVoice,
                        contentDescription = "Voice input"
                    )
                }
            }

            if (isListening) {
                Text(
                    text = "Listening…",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            voiceError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

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
                    .heightIn(min = 48.dp)
            ) {
                Text("Save item")
            }
        }
    }
}

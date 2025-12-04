package week11.st910491.finalproject.ui.addedit

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.ui.common.CategoryHelper
import week11.st910491.finalproject.ui.common.SpeechToTextParser
import week11.st910491.finalproject.ui.shoppinglist.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    navController: NavHostController,
    itemId: String?, // null = Add, not null = Edit
    viewModel: ShoppingListViewModel = viewModel()
) {
    val context = LocalContext.current

    // --- 1. SPEECH TO TEXT SETUP ---
    val speechParser = remember { SpeechToTextParser(context) }
    val speechState by speechParser.state.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                speechParser.startListening()
            }
        }
    )

    // Form state
    var nameInput by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("1") }
    var categoryInput by rememberSaveable { mutableStateOf("") }
    var notesInput by rememberSaveable { mutableStateOf("") }

    // --- 2. VOICE RESULT HANDLER (UPDATED FOR QUANTITY) ---
    LaunchedEffect(speechState.spokenText) {
        if (speechState.spokenText.isNotEmpty()) {
            val spoken = speechState.spokenText

            // A. Attempt to find digits (e.g. "5 apples")
            val digitRegex = Regex("(\\d+)")
            val digitMatch = digitRegex.find(spoken)

            // B. Attempt to find number words (e.g. "five apples")
            // Simple map for common small quantities
            val numberWords = mapOf(
                "one" to "1", "two" to "2", "three" to "3", "four" to "4", "five" to "5",
                "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9", "ten" to "10",
                "eleven" to "11", "twelve" to "12"
            )
            var wordMatchPair: Pair<String, String>? = null
            for ((word, digit) in numberWords) {
                // Check for whole words only to avoid matching "one" inside "bone"
                if (Regex("\\b$word\\b", RegexOption.IGNORE_CASE).containsMatchIn(spoken)) {
                    wordMatchPair = word to digit
                    break
                }
            }

            // C. Extract logic
            if (digitMatch != null) {
                // Case 1: Found digits (e.g., "5")
                val quantity = digitMatch.value
                quantityInput = quantity
                // Remove the number from the string to get the name
                nameInput = spoken.replaceFirst(quantity, "").trim()
            } else if (wordMatchPair != null) {
                // Case 2: Found number word (e.g., "five")
                val (word, digit) = wordMatchPair
                quantityInput = digit
                // Remove the word from the string (case-insensitive removal)
                nameInput = spoken.replace(Regex("\\b$word\\b", RegexOption.IGNORE_CASE), "").trim()
            } else {
                // Case 3: No quantity found, just text
                nameInput = spoken
            }

            // D. Trigger Smart Categorization on the extracted Name
            val suggested = CategoryHelper.getCategoryFor(nameInput)
            if (suggested != null) {
                categoryInput = suggested
            }
        }
    }

    // Load existing item if in "Edit" mode
    LaunchedEffect(itemId) {
        if (itemId != null) {
            // Placeholder: Load item logic here if needed
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
                    onValueChange = { newName ->
                        nameInput = newName
                        // Manual typing categorization
                        val suggested = CategoryHelper.getCategoryFor(newName)
                        if (suggested != null) {
                            categoryInput = suggested
                        }
                    },
                    label = { Text("Item Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.weight(1f)
                )

                // Voice Input Button
                IconButton(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            if (speechState.isListening) {
                                speechParser.stopListening()
                            } else {
                                speechParser.startListening()
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardVoice,
                        contentDescription = if (speechState.isListening) "Stop voice input" else "Start voice input",
                        tint = if (speechState.isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Feedback UI
            if (speechState.isListening) {
                Text(
                    text = "Listening... (Try saying '5 Apples')",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            speechState.error?.let { error ->
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
                    .heightIn(min = 48.dp)
            ) {
                Text("Save item")
            }
        }
    }
}
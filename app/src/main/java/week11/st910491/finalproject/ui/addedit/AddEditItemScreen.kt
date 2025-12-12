package week11.st910491.finalproject.ui.addedit

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    navController: NavHostController,
    itemId: String?, // null = Add, not null = Edit
    viewModel: ShoppingListViewModel = viewModel()
) {
    val context = LocalContext.current

    // --- SHARED STYLING CONSTANTS ---
    val primaryColor = Color(0xFF3F51B5) // Deep Indigo
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE3F2FD), Color(0xFFF3E5F5))
    )

    // --- 1. SPEECH TO TEXT SETUP ---
    val speechParser = remember { SpeechToTextParser(context) }
    val speechState by speechParser.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose { speechParser.shutdown() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) speechParser.startListening()
        }
    )

    // Form state
    var nameInput by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("1") }
    var categoryInput by rememberSaveable { mutableStateOf("") }
    var notesInput by rememberSaveable { mutableStateOf("") }

    // Optional: Loading state for Edit mode
    var isLoading by remember { mutableStateOf(false) }

    // --- 2. VOICE RESULT HANDLER ---
    LaunchedEffect(speechState.spokenText) {
        if (speechState.spokenText.isNotEmpty()) {
            val spoken = speechState.spokenText
            val digitRegex = Regex("(\\d+)")
            val digitMatch = digitRegex.find(spoken)

            val numberWords = mapOf(
                "one" to "1", "two" to "2", "three" to "3", "four" to "4", "five" to "5",
                "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9", "ten" to "10",
                "eleven" to "11", "twelve" to "12"
            )
            var wordMatchPair: Pair<String, String>? = null
            for ((word, digit) in numberWords) {
                if (Regex("\\b$word\\b", RegexOption.IGNORE_CASE).containsMatchIn(spoken)) {
                    wordMatchPair = word to digit
                    break
                }
            }

            if (digitMatch != null) {
                val quantity = digitMatch.value
                quantityInput = quantity
                nameInput = spoken.replaceFirst(quantity, "").trim()
            } else if (wordMatchPair != null) {
                val (word, digit) = wordMatchPair
                quantityInput = digit
                nameInput = spoken.replace(Regex("\\b$word\\b", RegexOption.IGNORE_CASE), "").trim()
            } else {
                nameInput = spoken
            }

            val suggested = CategoryHelper.getCategoryFor(nameInput)
            if (suggested != null) categoryInput = suggested
        }
    }

    // Load existing item if in "Edit" mode
    LaunchedEffect(itemId) {
        if (itemId != null) {
            isLoading = true
            val item = viewModel.getItemById(itemId)
            if (item != null) {
                nameInput = item.name
                quantityInput = item.quantity.toString()
                categoryInput = item.category
                notesInput = item.notes
            }
            isLoading = false
        }
    }

    // --- MAIN UI ---
    // Wrap Scaffold in Box to apply gradient to the whole screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent, // Transparent so Box gradient shows through
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (itemId == null) "Add Item" else "Edit Item",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Cancel", color = primaryColor, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
            // Removed FloatingActionButton to use a clean main button at the bottom
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()), // Make form scrollable
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter details below or use the microphone to add items quickly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                // --- 1. ITEM NAME WITH VOICE ---
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { newName ->
                        nameInput = newName
                        val suggested = CategoryHelper.getCategoryFor(newName)
                        if (suggested != null) categoryInput = suggested
                    },
                    label = { Text("Item Name") },
                    placeholder = { Text("e.g., Milk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Color.Gray)
                    },
                    trailingIcon = {
                        // Integrated Voice Button
                        IconButton(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    if (speechState.isListening) speechParser.stopListening() else speechParser.startListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardVoice,
                                contentDescription = "Voice Input",
                                tint = if (speechState.isListening) Color.Red else primaryColor
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = primaryColor
                    )
                )

                // Voice Feedback
                if (speechState.isListening) {
                    Text(
                        text = "Listening... (Try saying '5 Apples')",
                        style = MaterialTheme.typography.labelMedium,
                        color = primaryColor
                    )
                }
                speechState.error?.let {
                    Text(text = it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }


                // --- 2. QUANTITY ---
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it },
                    label = { Text("Quantity") },
                    leadingIcon = {
                        // Tag icon looks like a # hash, good for quantity/numbers
                        Icon(Icons.Filled.Tag, contentDescription = null, tint = Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = primaryColor
                    )
                )

                // --- 3. CATEGORY ---
                OutlinedTextField(
                    value = categoryInput,
                    onValueChange = { categoryInput = it },
                    label = { Text("Category") },
                    placeholder = { Text("Auto-filled") },
                    leadingIcon = {
                        Icon(Icons.Filled.Category, contentDescription = null, tint = Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = primaryColor
                    )
                )

                // --- 4. NOTES ---
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Notes (Optional)") },
                    leadingIcon = {
                        Icon(Icons.Filled.Description, contentDescription = null, tint = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Taller for notes
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = primaryColor
                    )
                )

                Spacer(modifier = Modifier.weight(1f)) // Push button to bottom if space allows

                // --- 5. SAVE BUTTON ---
                Button(
                    onClick = {
                        val qty = quantityInput.toIntOrNull() ?: 1
                        val uniqueId = itemId ?: UUID.randomUUID().toString()

                        val baseItem = ShoppingItem(
                            id = uniqueId,
                            name = nameInput.trim(),
                            quantity = qty,
                            category = categoryInput.trim(),
                            notes = notesInput.trim(),
                            isPurchased = false // Note: Resetting purchase status on edit
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
                        .height(56.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (itemId == null) "Save Item" else "Update Item",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
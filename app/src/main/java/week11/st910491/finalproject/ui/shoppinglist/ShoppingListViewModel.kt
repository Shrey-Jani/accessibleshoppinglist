package week11.st910491.finalproject.ui.shoppinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import week11.st910491.finalproject.data.shopping.FirestoreShoppingListRepository
// import week11.st910491.finalproject.data.shopping.RoomShoppingListRepository // <-- UNCOMMENT IF YOU USE ROOM
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.ui.common.UiState

class ShoppingListViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(UiState<List<ShoppingItem>>(isLoading = true))
    val uiState: StateFlow<UiState<List<ShoppingItem>>> = _uiState

    init {
        loadItems()
    }

    /**
     * Helper to get the repository dynamically.
     * This fixes the bug where logging in *after* opening the app resulted in a null repo.
     */
    private fun getRepository(): FirestoreShoppingListRepository? {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            FirestoreShoppingListRepository(currentUser.uid)
        } else {
            null
        }
    }

    private fun loadItems() {
        val repo = getRepository()

        if (repo == null) {
            // User is not logged in
            _uiState.value = UiState(isLoading = false, data = emptyList(), errorMessage = "Please log in to see your list.")
            return
        }

        viewModelScope.launch {
            repo.getItems()
                .catch { e ->
                    Log.e("ShoppingViewModel", "Error loading items", e)
                    _uiState.value = UiState(
                        isLoading = false,
                        data = emptyList(),
                        errorMessage = e.message ?: "Error loading items"
                    )
                }
                .collectLatest { list ->
                    _uiState.value = UiState(
                        isLoading = false,
                        data = list,
                        errorMessage = null
                    )
                }
        }
    }

    // NEW: overload used by AddEditItemScreen (passes full ShoppingItem)
    fun addItem(item: ShoppingItem) {
        val repo = getRepository()

        // Validation: User must be logged in
        if (repo == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "You must be logged in to save items.")
            return
        }

        if (item.name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Item name cannot be empty.")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("ShoppingViewModel", "Attempting to save item: ${item.name} with ID: ${item.id}")
                repo.addItem(item)
                Log.d("ShoppingViewModel", "Item saved successfully to Firestore.")
                // Clear error message on success
                _uiState.value = _uiState.value.copy(errorMessage = null)
            } catch (e: Exception) {
                Log.e("ShoppingViewModel", "Error adding item", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error adding item"
                )
            }
        }
    }

    fun deleteItem(item: ShoppingItem) {
        val repo = getRepository() ?: return
        if (item.id.isBlank()) return

        viewModelScope.launch {
            try {
                repo.deleteItem(item.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error deleting item"
                )
            }
        }
    }

    fun togglePurchased(item: ShoppingItem) {
        val repo = getRepository() ?: return
        if (item.id.isBlank()) return

        viewModelScope.launch {
            try {
                val updated = item.copy(isPurchased = !item.isPurchased)
                repo.updateItem(updated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error updating item"
                )
            }
        }
    }

    fun updateItem(item: ShoppingItem) {
        val repo = getRepository() ?: return
        if (item.id.isBlank()) return

        viewModelScope.launch {
            try {
                repo.updateItem(item)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error updating item"
                )
            }
        }
    }

    suspend fun getItemById(id: String): ShoppingItem? {
        val repo = getRepository()
        return repo?.getItemById(id)
    }
}
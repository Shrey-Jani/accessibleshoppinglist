package week11.st910491.finalproject.ui.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import week11.st910491.finalproject.data.shopping.FirestoreShoppingListRepository
import week11.st910491.finalproject.data.shopping.ShoppingListRepository
import week11.st910491.finalproject.domain.model.ShoppingItem
import week11.st910491.finalproject.ui.common.UiState

class ShoppingListViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userId: String? = auth.currentUser?.uid

    // If userId is null (logout), repository will simply be null, but no error is thrown
    private val repository: ShoppingListRepository? =
        userId?.let { FirestoreShoppingListRepository(it) }

    private val _uiState =
        MutableStateFlow(UiState<List<ShoppingItem>>(isLoading = true))
    val uiState: StateFlow<UiState<List<ShoppingItem>>> = _uiState

    init {
        // DO NOT show any error if repository is null → this fixes logout
        if (repository != null) {
            loadItems()
        } else {
            // Just show empty UI (this screen will disappear after navigation anyway)
            _uiState.value = UiState(
                isLoading = false,
                data = emptyList(),
                errorMessage = null
            )
        }
    }

    private fun loadItems() {
        val repo = repository ?: return
        viewModelScope.launch {
            repo.getItems()
                .catch { e ->
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

    // Existing version used by your old Add screen (string params)
    fun addItem(name: String, quantity: Int, category: String, notes: String) {
        val repo = repository ?: return   // Safe on logout
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                val item = ShoppingItem(
                    name = name,
                    quantity = quantity,
                    category = category,
                    notes = notes
                )
                repo.addItem(item)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error adding item"
                )
            }
        }
    }

    // NEW: overload used by AddEditItemScreen (passes full ShoppingItem)
    fun addItem(item: ShoppingItem) {
        val repo = repository ?: return
        if (item.name.isBlank()) return

        viewModelScope.launch {
            try {
                repo.addItem(item)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error adding item"
                )
            }
        }
    }

    fun deleteItem(item: ShoppingItem) {
        val repo = repository ?: return  // Safe on logout
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
        val repo = repository ?: return  // Safe on logout
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

    // NEW: update used by Edit mode
    fun updateItem(item: ShoppingItem) {
        val repo = repository ?: return
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

    // NEW: fetch single item for Edit screen (by id)
    suspend fun getItemById(id: String): ShoppingItem? {
        val repo = repository
        // Only FirestoreShoppingListRepository exposes getItemById
        return if (repo is FirestoreShoppingListRepository) {
            repo.getItemById(id)
        } else {
            null
        }
    }
}

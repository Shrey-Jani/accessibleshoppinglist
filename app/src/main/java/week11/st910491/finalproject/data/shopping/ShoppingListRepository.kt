package week11.st910491.finalproject.data.shopping

import kotlinx.coroutines.flow.Flow
import week11.st910491.finalproject.domain.model.ShoppingItem

interface ShoppingListRepository {
    fun getItems(): Flow<List<ShoppingItem>>
    suspend fun addItem(item: ShoppingItem)
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteItem(id: String)
}

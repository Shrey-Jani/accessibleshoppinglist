package week11.st910491.finalproject.data.shopping

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st910491.finalproject.domain.model.ShoppingItem

class FirestoreShoppingListRepository(
    private val userId: String,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ShoppingListRepository {

    private val itemsCollection = db
        .collection("users")
        .document(userId)
        .collection("items")

    override fun getItems(): Flow<List<ShoppingItem>> = callbackFlow {
        val listenerRegistration = itemsCollection
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.map { doc ->
                        ShoppingItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            quantity = (doc.getLong("quantity") ?: 1L).toInt(),
                            isPurchased = doc.getBoolean("isPurchased") ?: false,
                            category = doc.getString("category") ?: "",
                            notes = doc.getString("notes") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0L
                        )
                    }
                    trySend(items).isSuccess
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun addItem(item: ShoppingItem) {
        val data = mapOf(
            "name" to item.name,
            "quantity" to item.quantity,
            "isPurchased" to item.isPurchased,
            "category" to item.category,
            "notes" to item.notes,
            "createdAt" to item.createdAt
        )
        itemsCollection.add(data).await()
    }

    override suspend fun updateItem(item: ShoppingItem) {
        if (item.id.isBlank()) return
        val data = mapOf(
            "name" to item.name,
            "quantity" to item.quantity,
            "isPurchased" to item.isPurchased,
            "category" to item.category,
            "notes" to item.notes,
            "createdAt" to item.createdAt
        )
        itemsCollection.document(item.id).set(data).await()
    }

    override suspend fun deleteItem(id: String) {
        if (id.isBlank()) return
        itemsCollection.document(id).delete().await()
    }

    // NEW: fetch single item for Edit screen
    suspend fun getItemById(id: String): ShoppingItem? {
        if (id.isBlank()) return null
        val doc = itemsCollection.document(id).get().await()
        if (!doc.exists()) return null

        return ShoppingItem(
            id = doc.id,
            name = doc.getString("name") ?: "",
            quantity = (doc.getLong("quantity") ?: 1L).toInt(),
            isPurchased = doc.getBoolean("isPurchased") ?: false,
            category = doc.getString("category") ?: "",
            notes = doc.getString("notes") ?: "",
            createdAt = doc.getLong("createdAt") ?: 0L
        )
    }

    // --- ANALYTICS & SESSION MANAGEMENT ---

    suspend fun saveShoppingSession(session: week11.st910491.finalproject.domain.model.ShoppingSession) {
        val sessionsCollection = db
            .collection("users")
            .document(userId)
            .collection("sessions")

        val data = mapOf(
            "date" to session.date,
            "totalItems" to session.totalItems,
            "categorySummary" to session.categorySummary
        )
        sessionsCollection.add(data).await()
    }

    suspend fun clearAllItems() {
        // Firestore batch delete
        val snapshot = itemsCollection.get().await()
        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }
}

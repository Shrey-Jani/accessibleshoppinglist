package week11.st910491.finalproject.domain.model

data class ShoppingItem(
    val id: String = "",              // Firestore document ID
    val name: String = "",
    val quantity: Int = 1,
    val isPurchased: Boolean = false,
    val category: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()   // stored as a timestamp (ms) in Firestore
)

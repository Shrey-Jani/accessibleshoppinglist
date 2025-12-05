package week11.st910491.finalproject.domain.model

data class ShoppingSession(
    val id: String = "",
    val date: Long = System.currentTimeMillis(),
    val totalItems: Int = 0,
    val categorySummary: Map<String, Int> = emptyMap() // e.g., "Dairy" -> 2
)

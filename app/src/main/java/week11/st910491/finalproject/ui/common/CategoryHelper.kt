package week11.st910491.finalproject.ui.common

object CategoryHelper {
    private val categoryMap = mapOf(
        "Fruits" to listOf("apple", "banana", "orange", "grape", "mango", "berry", "melon", "lemon", "lime"),
        "Vegetables" to listOf("carrot", "potato", "onion", "tomato", "spinach", "broccoli", "pepper", "cucumber", "lettuce", "garlic"),
        "Dairy" to listOf("milk", "cheese", "butter", "yogurt", "cream", "egg", "marg", "curd"),
        "Bakery" to listOf("bread", "bagel", "croissant", "cake", "muffin", "doughnut", "bun", "toast", "pita"),
        "Beverages" to listOf("juice", "soda", "water", "coffee", "tea", "coke", "wine", "beer", "drink"),
        "Meat" to listOf("chicken", "beef", "pork", "steak", "fish", "salmon", "tuna", "ham", "sausage", "bacon"),
        "Pantry" to listOf("rice", "pasta", "noodle", "oil", "sugar", "salt", "flour", "spice", "sauce", "can"),
        "Household" to listOf("soap", "shampoo", "paper", "towel", "clean", "detergent", "trash")
    )

    fun getCategoryFor(itemName: String): String? {
        val lowerName = itemName.lowercase().trim()
        for ((category, keywords) in categoryMap) {
            if (keywords.any { lowerName.contains(it) }) {
                return category
            }
        }
        return null // No match found
    }
}
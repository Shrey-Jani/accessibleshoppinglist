package week11.st910491.finalproject.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import week11.st910491.finalproject.domain.model.ShoppingSession

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val totalTrips: Int = 0,
    val totalItems: Int = 0,
    val categoryDistribution: Map<String, Int> = emptyMap(),
    val errorMessage: String? = null
)

class AnalyticsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = AnalyticsUiState(isLoading = false, errorMessage = "User not logged in")
            return
        }

        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .document(userId)
                    .collection("sessions")
                    .get()
                    .await()

                val sessions = snapshot.documents.mapNotNull { doc ->
                    // Manual mapping since we didn't use a dedicated data class for Firestore serialization in the repo
                    // But we can try to map to ShoppingSession if fields match
                    val totalItems = (doc.getLong("totalItems") ?: 0).toInt()
                    val catSummary = doc.get("categorySummary") as? Map<String, Long> ?: emptyMap()
                    
                    // Convert Map<String, Long> to Map<String, Int>
                    val intMap = catSummary.mapValues { it.value.toInt() }

                    ShoppingSession(
                        id = doc.id,
                        totalItems = totalItems,
                        categorySummary = intMap
                    )
                }

                // Calculate Aggregates
                val totalTrips = sessions.size
                val totalItemsAllTime = sessions.sumOf { it.totalItems }
                
                // Merge all category maps
                val globalCategoryMap = mutableMapOf<String, Int>()
                sessions.forEach { session ->
                    session.categorySummary.forEach { (cat, count) ->
                        globalCategoryMap[cat] = (globalCategoryMap[cat] ?: 0) + count
                    }
                }
                
                // Sort by count descending
                val sortedCategories = globalCategoryMap.toList()
                    .sortedByDescending { (_, value) -> value }
                    .toMap()

                _uiState.value = AnalyticsUiState(
                    isLoading = false,
                    totalTrips = totalTrips,
                    totalItems = totalItemsAllTime,
                    categoryDistribution = sortedCategories
                )

            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState(
                    isLoading = false,
                    errorMessage = "Failed to load analytics: ${e.message}"
                )
            }
        }
    }
}

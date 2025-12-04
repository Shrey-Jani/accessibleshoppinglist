package week11.st910491.finalproject.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AccessibleCard(
    modifier: Modifier = Modifier,
    isHighContrast: Boolean = false,
    contentDesc: String? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp) // Modern rounded corners
    val borderModifier = if (isHighContrast) {
        modifier.border(2.dp, Color.White, shape) // White border for visibility on black
    } else {
        modifier // No border in premium mode, just shadow
    }

    Card(
        modifier = borderModifier.then(
            if (contentDesc != null) Modifier.semantics {
                this.contentDescription = contentDesc
            } else Modifier
        ),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighContrast) 0.dp else 4.dp), // Soft shadow
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(12.dp)) { // More breathing room
            content()
        }
    }
}

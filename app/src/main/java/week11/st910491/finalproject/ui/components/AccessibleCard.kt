package week11.st910491.finalproject.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val shape = RoundedCornerShape(8.dp)
    val borderModifier = if (isHighContrast) {
        modifier.border(2.dp, Color.Black, shape)
    } else modifier

    Card(
        modifier = borderModifier.then(
            if (contentDesc != null) Modifier.semantics {
                this.contentDescription = contentDesc
            } else Modifier
        ),
        colors = CardDefaults.cardColors()
    ) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

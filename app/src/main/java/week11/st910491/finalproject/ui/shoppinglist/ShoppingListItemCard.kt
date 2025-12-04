package week11.st910491.finalproject.ui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import week11.st910491.finalproject.domain.model.ShoppingItem

@Composable
fun ShoppingListItemCard(
    item: ShoppingItem,
    onTogglePurchased: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    isHighContrast: Boolean = false,
    isLargeText: Boolean = false
) {
    // We don't need the Card wrapper here anymore because AccessibleCard wraps this content
    // But we keep the Row layout
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp), // Inner padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = item.isPurchased,
            onCheckedChange = { onTogglePurchased() },
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp)) // More space

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = if (isLargeText) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, // Bold text
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (item.isPurchased) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                color = if (item.isPurchased) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Quantity: ${item.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete ${item.name}",
                tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.error // Yellow in high contrast
            )
        }
    }
}

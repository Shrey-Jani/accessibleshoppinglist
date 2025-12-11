package week11.st910491.finalproject.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighContrast: Boolean = false,
    isLargeText: Boolean = false,
    contentDesc: String? = null
) {
    val shape = RoundedCornerShape(8.dp)
    val minHeight = 48.dp
    val border = if (isHighContrast) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null
    val colors = if (isHighContrast) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        )
    } else {
        ButtonDefaults.buttonColors()
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = minHeight)
            .padding(0.dp)
            .semantics {
                // Always mark as button for TalkBack, even if we don't override description
                this.role = Role.Button
                if (contentDesc != null) {
                    this.contentDescription = contentDesc
                }
            },
        shape = shape,
        border = border,
        colors = colors
    ) {
        Text(
            text = text,
            style = if (isLargeText) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
    }
}

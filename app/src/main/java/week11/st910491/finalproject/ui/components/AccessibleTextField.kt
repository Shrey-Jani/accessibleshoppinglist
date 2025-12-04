package week11.st910491.finalproject.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AccessibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isLargeText: Boolean = false,
    contentDesc: String? = null
) {
    val minHeight = 56.dp
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = if (isLargeText) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium) },
        modifier = modifier
            .heightIn(min = minHeight)
            .padding(0.dp)
            .then(
                if (contentDesc != null) Modifier.semantics {
                    this.contentDescription = contentDesc
                } else Modifier
            ),
        textStyle = if (isLargeText) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        singleLine = true
    )
}

package week11.st910491.finalproject

import android.R.attr.scaleX
import android.R.attr.scaleY
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import week11.st910491.finalproject.navigation.AppNavHost
import week11.st910491.finalproject.ui.auth.AuthViewModel
import week11.st910491.finalproject.ui.theme.AccessibleShoppingListTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferencesRepository = week11.st910491.finalproject.data.UserPreferencesRepository(this)

        setContent {
            val isHighContrast by userPreferencesRepository.isHighContrast.collectAsState(initial = false)
            val isLargeText by userPreferencesRepository.isLargeText.collectAsState(initial = false)
            val isOneHanded by userPreferencesRepository.isOneHanded.collectAsState(initial = false)
            val hasSeenOnboarding by userPreferencesRepository.hasSeenOnboarding.collectAsState(initial = false)

            AccessibleShoppingListTheme(
                highContrast = isHighContrast,
                largeText = isLargeText
            ) {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                Surface(color = MaterialTheme.colorScheme.background) {
                    // GLOBAL ONE-HANDED MODE CONTAINER
                    // This wraps the ENTIRE app to apply the "Mini Screen" effect globally.
                    
                    // State for alignment (Left/Right) - persisted in memory for session
                    var oneHandedAlignment by remember { mutableStateOf(Alignment.BottomEnd) }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isOneHanded) Color.Black.copy(alpha = 0.6f) else Color.Transparent)
                    ) {
                        // SCALED CONTENT
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    if (isOneHanded) {
                                        scaleX = 0.85f
                                        scaleY = 0.85f
                                        transformOrigin = if (oneHandedAlignment == Alignment.BottomEnd) {
                                            TransformOrigin(1f, 1f)
                                        } else {
                                            TransformOrigin(0f, 1f)
                                        }
                                    }
                                }
                        ) {
                            AppNavHost(
                                navController = navController,
                                authViewModel = authViewModel,
                                userPreferencesRepository = userPreferencesRepository,
                                isOneHanded = isOneHanded,
                                hasSeenOnboarding = hasSeenOnboarding
                            )
                        }

                        // GLOBAL TOGGLE BUTTON (Overlay)
                        if (isOneHanded) {
                            androidx.compose.material3.IconButton(
                                onClick = {
                                    oneHandedAlignment = if (oneHandedAlignment == Alignment.BottomEnd) {
                                        Alignment.BottomStart
                                    } else {
                                        Alignment.BottomEnd
                                    }
                                },
                                modifier = Modifier
                                    .align(if (oneHandedAlignment == Alignment.BottomEnd) Alignment.CenterStart else Alignment.CenterEnd)
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = if (oneHandedAlignment == Alignment.BottomEnd) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                                    contentDescription = "Switch Side",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

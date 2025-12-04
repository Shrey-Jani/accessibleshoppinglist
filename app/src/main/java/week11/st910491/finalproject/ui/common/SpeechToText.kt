package week11.st910491.finalproject.ui.common

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberSpeechToText(
    onResult: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val activity = context as Activity

    var lastText by remember { mutableStateOf("") }

    val speechLauncher: ActivityResultLauncher<Intent> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                // Safely pick the first match (if any) instead of indexing directly.
                val firstMatch = matches?.firstOrNull()
                if (!firstMatch.isNullOrBlank()) {
                    lastText = firstMatch
                    onResult(lastText)
                }
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startSpeechRecognition(activity, speechLauncher)
            } else {
                Toast.makeText(
                    activity,
                    "Microphone permission is required for speech input",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // This is the lambda you call from your Mic button
    return {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(
                context,
                "Speech recognition is not available on this device/emulator.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                startSpeechRecognition(activity, speechLauncher)
            } else {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}

private fun startSpeechRecognition(
    activity: Activity,
    speechLauncher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
    }
    speechLauncher.launch(intent)
}

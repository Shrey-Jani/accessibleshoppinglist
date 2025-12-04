package week11.st910491.finalproject.ui.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechToTextParser(private val context: Context) {

    private val _state = MutableStateFlow(SpeechState())
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    private var recognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun startListening() {
        _state.value = SpeechState(isListening = true)

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.value = SpeechState(error = "Speech recognition is not available on this device.")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _state.value = _state.value.copy(isListening = false)
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found."
                    SpeechRecognizer.ERROR_NETWORK -> "Network error."
                    else -> "Error occurred. Please try again."
                }
                _state.value = SpeechState(error = message, isListening = false)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _state.value = SpeechState(spokenText = matches[0], isListening = false)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    fun stopListening() {
        _state.value = _state.value.copy(isListening = false)
        recognizer.stopListening()
    }

    fun shutdown() {
        recognizer.destroy()
    }
}

data class SpeechState(
    val spokenText: String = "",
    val isListening: Boolean = false,
    val error: String? = null
)
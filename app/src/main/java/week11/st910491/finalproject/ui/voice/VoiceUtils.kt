package week11.st910491.finalproject.ui.voice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VoiceRecognizerManager(
    private val activity: Activity,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onListeningStateChanged: (Boolean) -> Unit
) : RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(activity)) {
            onError("Speech recognition not available on this device")
            return
        }

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity).apply {
                setRecognitionListener(this@VoiceRecognizerManager)
            }
        }

        val locale = Locale.getDefault()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, locale)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.packageName)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the item name")
        }

        onListeningStateChanged(true)
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        onListeningStateChanged(false)
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    override fun onReadyForSpeech(params: Bundle?) {
        onListeningStateChanged(true)
    }

    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {
        onListeningStateChanged(false)
    }

    override fun onError(error: Int) {
        onListeningStateChanged(false)
        Log.d("VoiceRecognizer", "onError code=$error")

        val msg = when (error) {
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                "Network error while recognizing speech"
            SpeechRecognizer.ERROR_NO_MATCH ->
                "Could not understand. Please try again."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                "No speech detected. Try again."
            SpeechRecognizer.ERROR_CLIENT ->
                "Speech recognition client error"
            else -> "Speech recognition error ($error)"
        }
        onError(msg)
    }

    override fun onResults(results: Bundle?) {
        onListeningStateChanged(false)
        val text = results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
            ?.trim()
            ?: ""

        Log.d("VoiceRecognizer", "onResults='$text'")

        if (text.isNotEmpty()) {
            onResult(text)
        } else {
            onError("Could not understand. Please try again.")
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}

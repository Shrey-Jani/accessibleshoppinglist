package week11.st910491.finalproject.ui.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        // Initialize the TTS engine
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try to set the language to the device default
                val result = tts?.setLanguage(Locale.getDefault())

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS_DEBUG", "Language missing or not supported on this device.")
                } else {
                    isInitialized = true
                    Log.d("TTS_DEBUG", "TTS Initialized successfully!")
                }
            } else {
                Log.e("TTS_DEBUG", "TTS Initialization failed completely.")
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            Log.e("TTS_DEBUG", "Cannot speak yet - TTS is not ready.")
            return
        }

        Log.d("TTS_DEBUG", "Speaking text: $text")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
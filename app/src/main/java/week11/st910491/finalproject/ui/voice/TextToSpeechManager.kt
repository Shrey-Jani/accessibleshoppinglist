package week11.st910491.finalproject.ui.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(context: Context) {
    private var tts: TextToSpeech? = null
    var isInitialized = false
        private set

    init {
        // Use applicationContext to avoid leaking Activity context
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // FIX: Emulators often fail with Locale.getDefault().
                // Force Canada's English to ensure it works during testing.
                val result = tts?.setLanguage(Locale.CANADA)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS_DEBUG", "Language missing or not supported. Try installing English via Emulator Settings.")
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
            Log.e("TTS_DEBUG", "Cannot speak yet - TTS is still initializing...")
            return
        }

        Log.d("TTS_DEBUG", "Speaking: $text")
        // QUEUE_ADD ensures it speaks even if another sound (like a click) just happened
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
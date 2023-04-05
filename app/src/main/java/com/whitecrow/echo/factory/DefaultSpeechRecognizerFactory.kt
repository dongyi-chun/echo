package com.whitecrow.echo.factory

import android.content.Context
import android.speech.SpeechRecognizer

class DefaultSpeechRecognizerFactory : SpeechRecognizerFactory {
    override fun createSpeechRecognizer(context: Context): SpeechRecognizer {
        return SpeechRecognizer.createSpeechRecognizer(context)
    }
}
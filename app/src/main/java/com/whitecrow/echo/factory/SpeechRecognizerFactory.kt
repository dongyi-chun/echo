package com.whitecrow.echo.factory

import android.content.Context
import android.speech.SpeechRecognizer

interface SpeechRecognizerFactory {
    fun createSpeechRecognizer(context: Context): SpeechRecognizer
}
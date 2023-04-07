package com.whitecrow.echo.model

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitecrow.echo.data.ChatMessage
import com.whitecrow.echo.repository.ChatGPTApi
import com.whitecrow.echo.repository.ChatGPTRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for a voice recognition
 */
class ChatViewModel(
    private val chatGPTRepository: ChatGPTRepository = ChatGPTRepository(ChatGPTApi.service)
) : ViewModel() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    private val _isListening = MutableLiveData(false)
    val isListening: LiveData<Boolean>
        get() = _isListening

    private val _chatMessages = MutableLiveData<List<ChatMessage>>(emptyList())
    val chatMessages: LiveData<List<ChatMessage>>
        get() = _chatMessages

    fun initialise(context: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognitionListener)
        }

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    override fun onCleared() {
        speechRecognizer.destroy()
        super.onCleared()
    }

    fun onSendMessage(input: String) {
        addInputMessageToChat(ChatMessage.Input(input.capitalize(Locale.current)))
        viewModelScope.launch {
            try {
                val response = chatGPTRepository.getChatGPTResponse(input)
                addOutputMessageToChat(response)
            } catch (e: Exception) {
                addOutputMessageToChat(ChatMessage.Output("Error: ${e.message}"))
            }
        }
    }

    private fun addInputMessageToChat(input: ChatMessage) {
        val currentMessages = _chatMessages.value.orEmpty()
        _chatMessages.value = currentMessages + input
    }

    private fun addOutputMessageToChat(output: ChatMessage) {
        val currentMessages = _chatMessages.value.orEmpty()
        _chatMessages.value = currentMessages + output
    }

    fun startListening() {
        if (_isListening.value == true) {
            // Stop listening if it's already listening
            stopListening()
        } else {
            // Start listening and update the listening status
            speechRecognizer.startListening(speechRecognizerIntent)
            _isListening.value = true
        }
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        _isListening.value = false
    }

    val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit

        override fun onBeginningOfSpeech() = Unit

        override fun onRmsChanged(rmsdB: Float) = Unit

        override fun onBufferReceived(buffer: ByteArray?) = Unit

        override fun onEndOfSpeech() = Unit

        override fun onError(error: Int) {
            _isListening.value = false
        }

        override fun onResults(results: Bundle?) {
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                if (it.isNotEmpty()) onSendMessage(it[0].capitalize(Locale.current))
            }
            _isListening.value = false
        }

        override fun onPartialResults(partialResults: Bundle?) {
            // TODO: Support partial results better
            /**
            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                if (it.isNotEmpty()) onSendMessage(it[0].capitalize(Locale.current))
            }
            */
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }
}
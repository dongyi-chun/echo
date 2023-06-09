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
import com.whitecrow.echo.repository.chat.ChatGPTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for a voice recognition
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    val chatGPTRepository: ChatGPTRepository
) : ViewModel() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    private val _isListening = MutableLiveData(false)
    val isListening: LiveData<Boolean>
        get() = _isListening

    private val _chatMessages = MutableLiveData<List<ChatMessage>>(emptyList())
    val chatMessages: LiveData<List<ChatMessage>>
        get() = _chatMessages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Add a property to store the index of the last partial result
    private var lastPartialResultIndex: Int? = null

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

    fun onSendMessage(input: String, currentMessages: List<ChatMessage> = mutableListOf()) {
        addInputMessageToChat(ChatMessage.Input(input.capitalize(Locale.current)), currentMessages)
        viewModelScope.launch {
            try {
                val response = chatGPTRepository.getChatGPTResponse(
                    input, currentMessages, _isLoading::postValue)
                addOutputMessageToChat(response)
            } catch (e: Exception) {
                addOutputMessageToChat(ChatMessage.Output("Error: ${e.message}"))
            } finally {
                // Make sure that a loading animation is hidden
                _isLoading.postValue(false)
            }
        }
    }

    private fun addInputMessageToChat(input: ChatMessage, currentMessages: List<ChatMessage>) {
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

        override fun onPartialResults(partialResults: Bundle?) {
            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                if (it.isNotEmpty()) {
                    val partialMessage = ChatMessage.Input(it[0].capitalize(Locale.current))
                    val currentMessages = _chatMessages.value.orEmpty()

                    if (lastPartialResultIndex == null) {
                        // If this is the first partial result, add it to the end of the list
                        _chatMessages.value = currentMessages + partialMessage
                        lastPartialResultIndex = currentMessages.size
                    } else {
                        // Otherwise, update the existing partial result
                        val updatedMessages = currentMessages.toMutableList()
                        updatedMessages[lastPartialResultIndex!!] = partialMessage
                        _chatMessages.value = updatedMessages
                    }
                }
            }
        }

        override fun onResults(results: Bundle?) {
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                if (it.isNotEmpty()) {
                    val finalMessage = it[0].capitalize(Locale.current)
                    val currentMessages = _chatMessages.value.orEmpty()
                    // Remove the last partial result from the list
                    if (lastPartialResultIndex != null) {
                        val messagesWithoutPartial = currentMessages.toMutableList().apply {
                            removeAt(lastPartialResultIndex!!)
                        }
                        onSendMessage(finalMessage, messagesWithoutPartial)
                        // Reset the lastPartialResultIndex to null
                        lastPartialResultIndex = null
                    } else {
                        onSendMessage(finalMessage)
                    }
                }
            }
            _isListening.value = false
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }
}
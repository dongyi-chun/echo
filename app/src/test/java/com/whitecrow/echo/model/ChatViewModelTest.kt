package com.whitecrow.echo.model

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.test.core.app.ApplicationProvider
import com.whitecrow.echo.data.ChatMessage
import com.whitecrow.echo.repository.chat.ChatGPTRepository
import com.whitecrow.echo.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ChatViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ChatViewModel
    private lateinit var context: Context

    private val repository: ChatGPTRepository = mockk()

    @Before
    fun setUp() {
        viewModel = ChatViewModel(repository)
        context = ApplicationProvider.getApplicationContext()

        // initialise the ViewModel
        viewModel.initialise(context)
    }

    @Test
    fun `should recognize spoken text`() {
        val text = "test spoken text"

        // simulate speech recognition by sending mocked results
        val results = Bundle().apply {
            putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(text))
        }
        viewModel.recognitionListener.onResults(results)

        // check if the ViewModel recognizes the spoken text correctly
        assertEquals(arrayListOf(ChatMessage.Input(text.capitalize())), viewModel.chatMessages.value)
    }

    @Test
    fun `should recognise speaking text`() {
        val text1 = "test speaking"
        val text2 = "test speaking text"

        // simulate speech recognition by sending mocked results
        val results1 = Bundle().apply {
            putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(text1))
        }
        viewModel.recognitionListener.onPartialResults(results1)

        // check if the ViewModel recognises the speaking text correctly
        assertEquals(arrayListOf(ChatMessage.Input(text1.capitalize())), viewModel.chatMessages.value)

        // simulate speech recognition by sending mocked longer results
        val results2 = Bundle().apply {
            putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(text2))
        }
        viewModel.recognitionListener.onPartialResults(results2)

        // check if the ViewModel recognises the speaking text correctly
        assertEquals(arrayListOf(ChatMessage.Input(text2.capitalize())), viewModel.chatMessages.value)
    }

    @Test
    fun `should update isListening when startListening is called`() {
        // check that isListening is initially false
        assertFalse(viewModel.isListening.value!!)

        // call startListening
        viewModel.startListening()

        // check that isListening is now true
        assertTrue(viewModel.isListening.value!!)
    }

    @Test
    fun `should update isListening to false when stopListening is called after startListening`() {
        // check that isListening is initially false
        assertFalse(viewModel.isListening.value!!)

        // call startListening
        viewModel.startListening()

        // check that isListening is now true
        assertTrue(viewModel.isListening.value!!)

        // call stopListening
        viewModel.stopListening()

        // check that isListening is now false
        assertFalse(viewModel.isListening.value!!)
    }

    @Test
    fun `should update responded text by repository`() = runTest {
        // Given
        val input = "Hello"
        val output = "Hi"
        val message = arrayListOf(ChatMessage.Input(input), ChatMessage.Output(output))
        val response = ChatMessage.Output("Hi")
        coEvery { repository.getChatGPTResponse(input) } returns response

        // When
        viewModel.onSendMessage(input)
        advanceUntilIdle()

        // Then
        assertEquals(message, viewModel.chatMessages.value)
    }
}
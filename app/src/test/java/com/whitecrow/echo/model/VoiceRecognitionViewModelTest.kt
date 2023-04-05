package com.whitecrow.echo.model

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VoiceRecognitionViewModelTest {

    private lateinit var viewModel: VoiceRecognitionViewModel
    private lateinit var context: Context

    @Before
    fun setUp() {
        viewModel = VoiceRecognitionViewModel()
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
        assertEquals(text.capitalize(), viewModel.recognisedText.value)
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
}
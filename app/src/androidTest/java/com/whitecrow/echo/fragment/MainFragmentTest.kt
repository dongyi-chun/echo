package com.whitecrow.echo.fragment

import android.view.ViewGroup
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whitecrow.echo.MainActivity
import com.whitecrow.echo.R
import com.whitecrow.echo.model.ChatViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = mockk<ChatViewModel>(relaxed = true)
    private val recognisedText = MutableLiveData("Test text")
    private val isListening = MutableLiveData(false)

    @Before
    fun setUp() {
        every { viewModel.recognisedText } returns recognisedText
        every { viewModel.isListening } returns isListening

        composeTestRule.runOnUiThread {
            // Attaching a test fragment
            val fragment = MainFragment.newInstance()
            composeTestRule.activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()

            // Prevent a duplicated setContent() call
            val root = composeTestRule.activity.findViewById<ViewGroup>(android.R.id.content)
            root.removeAllViews()

            composeTestRule.setContent {
                fragment.ChatScreen(viewModel)
            }
        }
    }

    @Test
    fun testRecognizedText_isDisplayed() {
        composeTestRule.onNodeWithText("Test text").assertIsDisplayed()
    }

    @Test
    fun testStartListeningButton_isDisplayed() {
        composeTestRule.onNodeWithText("Start Listening").assertIsDisplayed()
    }

    @Test
    fun testStopListeningButton_isDisplayed() {
        composeTestRule.runOnUiThread {
            isListening.value = true
        }
        composeTestRule.onNodeWithText("Stop Listening").assertIsDisplayed()
    }
}
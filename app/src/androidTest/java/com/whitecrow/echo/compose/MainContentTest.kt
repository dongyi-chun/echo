package com.whitecrow.echo.compose

import android.view.ViewGroup
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whitecrow.echo.MainActivity
import com.whitecrow.echo.data.ChatMessage
import com.whitecrow.echo.model.ChatViewModel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    val viewModel = mockk<ChatViewModel>(relaxed = true)

    private val chatMessages = MutableLiveData<List<ChatMessage>>(listOf(ChatMessage.Input("Test text")))
    private val isListening = MutableLiveData(false)

    @Before
    fun setUp() {
        hiltRule.inject()

        every { viewModel.chatMessages } returns chatMessages
        every { viewModel.isListening } returns isListening

        composeTestRule.runOnUiThread {
            // Prevent a duplicated setContent() call
            val root = composeTestRule.activity.findViewById<ViewGroup>(android.R.id.content)
            root.removeAllViews()

            composeTestRule.setContent {
                MainContent()
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

    @Test
    fun chatScreen_displaysRecognisedAndRespondedMessages() {
        // Given
        val recognisedMessage = ChatMessage.Input("Hello")
        val respondedMessage = ChatMessage.Output("Hi")

        // Prepare the chatMessages list
        val updatedChatMessages = listOf(recognisedMessage, respondedMessage)

        // Update the ViewModel's chatMessages value
        composeTestRule.runOnUiThread {
            chatMessages.value = updatedChatMessages
        }

        // Check that the recognisedMessage and respondedMessage are displayed
        composeTestRule.onNodeWithText(recognisedMessage.content).assertIsDisplayed()
        composeTestRule.onNodeWithText(respondedMessage.content).assertIsDisplayed()
    }

    @Test
    fun chatScreen_scrollsToLatestMessage_whenAdded() {
        val updatedChatMessages = mutableListOf<ChatMessage>()

        // Add messages
        repeat(50) { index ->
            // Prepare the chatMessages list
            updatedChatMessages.add(ChatMessage.Input("Input message $index"))
            updatedChatMessages.add(ChatMessage.Output("Output message $index"))
        }
        updatedChatMessages.add(ChatMessage.Input("Last message"))

        // Update the ViewModel's chatMessages value
        composeTestRule.runOnUiThread {
            chatMessages.value = updatedChatMessages
        }
        composeTestRule.waitForIdle()

        // Check if the last message is displayed
        composeTestRule.onNodeWithText("Last message").assertExists()
    }
}
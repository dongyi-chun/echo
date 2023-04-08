package com.whitecrow.echo.repository.chat

import com.whitecrow.echo.data.ChatMessage
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGPTRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var chatGPTRepository: ChatGPTRepository
    private lateinit var chatGPTService: ChatGPTService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        chatGPTService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGPTService::class.java)

        chatGPTRepository = ChatGPTRepository(chatGPTService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getChatGPTResponse returns correct output`() = runBlocking {
        val input = "What's the weather like tomorrow?"
        val expectedOutput = "The weather tomorrow is expected to be sunny with a high of 25°C."

        prepareResponse(expectedOutput)

        val result = chatGPTRepository.getChatGPTResponse(input) {}
        assert(result.content == expectedOutput)
    }

    @Test
    fun `test onLoading value`() = runBlocking {
        // Given
        val input = "Hello, ChatGPT!"
        val expectedOutput = "Hello, User!"

        prepareResponse(expectedOutput)

        // When
        var loading = false
        val result = chatGPTRepository.getChatGPTResponse(input) { isLoading -> loading = isLoading }

        // Then
        assertEquals(ChatMessage.Output(expectedOutput), result)
        assertEquals(false, loading)
    }

    /**
     * Helpers
     */

    private fun prepareResponse(expectedOutput: String) {
        val jsonResponse = """
            {
              "id": "chatcmpl-123",
              "object": "chat.completion",
              "created": 1677652288,
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "$expectedOutput"
                  },
                  "finish_reason": "stop"
                }
              ],
              "usage": {
                "prompt_tokens": 9,
                "completion_tokens": 12,
                "total_tokens": 21
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))
    }
}
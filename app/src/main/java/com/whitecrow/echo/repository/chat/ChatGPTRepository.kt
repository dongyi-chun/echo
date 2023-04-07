package com.whitecrow.echo.repository.chat

import com.whitecrow.echo.data.ChatMessage

/**
 * Repository for ChatGPT
 */
class ChatGPTRepository(private val chatGPTService: ChatGPTService) {
    suspend fun getChatGPTResponse(input: String): ChatMessage.Output {
        val request = ChatGPTRequest(
            messages = listOf(
                Message(role = "user", content = input)
            )
        )
        val response = chatGPTService.getChatGPTResponse(request)
        return ChatMessage.Output(response.choices.first().message.content)
    }
}
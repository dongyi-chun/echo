package com.whitecrow.echo.repository.chat

import com.whitecrow.echo.data.ChatMessage

/**
 * Repository for ChatGPT
 */
class ChatGPTRepository(private val chatGPTService: ChatGPTService) {
    suspend fun getChatGPTResponse(
        input: String,
        onLoading: (Boolean) -> Unit
    ): ChatMessage.Output {
        onLoading(true)
        val request = ChatGPTRequest(
            messages = listOf(
                Message(role = "user", content = input)
            )
        )
        val response = chatGPTService.getChatGPTResponse(request)
        onLoading(false)
        return ChatMessage.Output(response.choices.first().message.content)
    }
}
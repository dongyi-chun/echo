package com.whitecrow.echo.repository.chat

import com.whitecrow.echo.data.ChatMessage

/**
 * Repository for ChatGPT
 */
class ChatGPTRepository(private val chatGPTService: ChatGPTService) {
    suspend fun getChatGPTResponse(
        input: String,
        currentMessages: List<ChatMessage>,
        onLoading: (Boolean) -> Unit
    ): ChatMessage.Output {
        onLoading(true)
        val request = ChatGPTRequest(
            messages = currentMessages.map {
                when (it) {
                    is ChatMessage.Input -> Message(role = "user", content = it.content)
                    is ChatMessage.Output -> Message(role = "system", content = it.content)
                }
            } + Message(role = "user", content = input)
        )
        val response = chatGPTService.getChatGPTResponse(request)
        onLoading(false)
        return ChatMessage.Output(response.choices.first().message.content)
    }
}
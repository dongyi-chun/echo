package com.whitecrow.echo.repository

import com.whitecrow.echo.data.ChatMessage
import com.whitecrow.echo.service.ChatGPTService

/**
 * Repository for ChatGPT
 */
class ChatGPTRepository(private val chatGPTService: ChatGPTService) {
    suspend fun getChatGPTResponse(input: String): ChatMessage =
        chatGPTService.getChatGPTResponse(input)
}
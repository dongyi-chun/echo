package com.whitecrow.echo.repository.chat

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Interface for ChatGPT
 */
interface ChatGPTService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun getChatGPTResponse(@Body request: ChatGPTRequest): ChatGPTResponse
}
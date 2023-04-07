package com.whitecrow.echo.service

import com.whitecrow.echo.data.ChatMessage
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for ChatGPT
 */
interface ChatGPTService {
    @GET("your_endpoint")
    suspend fun getChatGPTResponse(@Query("input") input: String): ChatMessage
}
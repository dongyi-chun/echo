package com.whitecrow.echo.repository

import com.whitecrow.echo.service.ChatGPTService

object ChatGPTApi {
    private const val BASE_URL = "https://your_chatgpt_api_base_url/"
    private val apiClient = ApiClient(BASE_URL)

    val service: ChatGPTService = apiClient.retrofit.create(ChatGPTService::class.java)
}
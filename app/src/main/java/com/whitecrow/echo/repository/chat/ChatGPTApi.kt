package com.whitecrow.echo.repository.chat

import com.whitecrow.echo.BuildConfig
import com.whitecrow.echo.repository.ApiClient

object ChatGPTApi {
    private const val OPENAI_BASE_URL = "https://api.openai.com/v1/"
    private val apiClient = ApiClient(OPENAI_BASE_URL, BuildConfig.API_KEY)

    val service: ChatGPTService = apiClient.retrofit.create(ChatGPTService::class.java)
}
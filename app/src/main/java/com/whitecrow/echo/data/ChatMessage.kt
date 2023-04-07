package com.whitecrow.echo.data

sealed class ChatMessage {
    abstract val content: String

    data class Input(override val content: String = "") : ChatMessage()
    data class Output(override val content: String = "") : ChatMessage()
}

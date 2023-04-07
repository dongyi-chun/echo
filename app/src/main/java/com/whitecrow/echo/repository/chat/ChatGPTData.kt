package com.whitecrow.echo.repository.chat

/**
 * Request data
 *
 * Sample:
 * {
 *   "model": "gpt-3.5-turbo",
 *   "messages": [{"role": "user", "content": "Hello!"}]
 * }
 */
data class ChatGPTRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

/**
 * Response data
 *
 * Sample:
 * {
 *   "id": "chatcmpl-123",
 *   "object": "chat.completion",
 *   "created": 1677652288,
 *   "choices": [{
 *     "index": 0,
 *     "message": {
 *       "role": "assistant",
 *       "content": "\n\nHello there, how may I assist you today?",
 *     },
 *     "finish_reason": "stop"
 *   }],
 *   "usage": {
 *      "prompt_tokens": 9,
 *      "completion_tokens": 12,
 *      "total_tokens": 21
 *   }
 * }
 */
data class ChatGPTResponse(
    val id: String,
    val obj: String,
    val created: Long,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

package com.rushikesh.pandu.data

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.rushikesh.pandu.BuildConfig

import com.rushikesh.pandu.model.PromptResponse

object AiModel {
    private var chat: Chat
    private val config = generationConfig {
        temperature = 0.7f
    }
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.apiKey,
        config
    )
    init {
        chat = generativeModel.startChat()
    }
    fun sendMessage(prompt: String): PromptResponse {
        val modelResponse = chat.sendMessageStream(prompt)
        val response = PromptResponse(modelResponse)
        return response
    }
}
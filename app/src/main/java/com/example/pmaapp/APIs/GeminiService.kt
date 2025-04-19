package com.example.pmaapp.services

import com.example.pmaapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiService {
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val textModel by lazy {
        GenerativeModel(modelName = "gemini-1.5-pro", apiKey = apiKey)
    }

    /**
     * Instead of a separate system-role message (unsupported),
     * we simply prepend our context to every user prompt.
     */
    private fun prependContext(userMessage: String, context: String?): String {
        return if (context.isNullOrBlank()) {
            userMessage
        } else {
            // Combine system/context + user into a single string
            "You are an assistant for a football manager app. $context\n\n$userMessage"
        }
    }

    /**
     * Streams the model’s response for one combined prompt string.
     */
    suspend fun sendMessage(
        message: String,
        context: String? = null
    ): Flow<String> = flow {
        val fullPrompt = prependContext(message, context)

        // Use the String overload: generateContentStream(prompt: String)
        val responseFlow = textModel.generateContentStream(fullPrompt)

        val sb = StringBuilder()
        responseFlow.collect { chunk ->
            sb.append(chunk.text ?: "")
            emit(sb.toString())
        }
    }
}

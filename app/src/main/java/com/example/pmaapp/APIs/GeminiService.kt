package com.example.pmaapp.services

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class GeminiService {
    // TODO: Replace with your new API key
    private val apiKey = "AIzaSyAffHB5J_ETsZ-XyuvUYavubJWivGk3jxY"

    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
    )

    // Using gemini-1.5-flash for better performance and higher free tier limits
    private val textModel by lazy {
        try {
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                safetySettings = safetySettings
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Error creating GenerativeModel", e)
            throw e
        }
    }

    private fun createCoachPepPrompt(userMessage: String, context: String?): String {
        return try {
            if (context.isNullOrBlank()) {
                userMessage
            } else {
                // Enhanced prompt structure for better Coach Pep responses
                """
                $context
                
                **Current Coach Question:** $userMessage
                
                **Instructions for Response:**
                - Respond as Coach Pep with professional authority and 15+ years experience
                - Structure your response with clear headings: **Situation Analysis**, **Tactical Options**, **Recommended Action**, **Pro Tip**
                - Provide at least 2 alternative solutions when applicable
                - Reference FIFA rules when relevant (or state to consult FIFA rulebook if uncertain)
                - End with a motivational coaching phrase
                - Use bullet points for multiple options
                - Keep the tone professional but encouraging
                
                Please analyze the situation step-by-step and provide your expert coaching advice:
                """.trimIndent()
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Error creating Coach Pep prompt", e)
            userMessage
        }
    }

    suspend fun sendMessage(
        message: String,
        context: String? = null
    ): Flow<String> = flow {
        try {
            if (message.isBlank()) {
                emit("Coach, please share your question or situation, and I'll provide you with tactical guidance!")
                return@flow
            }

            // Add delay to avoid rate limiting
            delay(1000) // 1 second delay between requests

            val fullPrompt = createCoachPepPrompt(message, context)
            Log.d("GeminiService", "Sending Coach Pep prompt with model: gemini-1.5-flash")

            val responseFlow = textModel.generateContentStream(fullPrompt)

            val sb = StringBuilder()
            var hasEmittedContent = false

            responseFlow.collect { chunk ->
                try {
                    val chunkText = chunk.text
                    if (!chunkText.isNullOrEmpty()) {
                        sb.append(chunkText)
                        emit(sb.toString())
                        hasEmittedContent = true
                    }
                } catch (e: Exception) {
                    Log.e("GeminiService", "Error processing chunk", e)
                    if (!hasEmittedContent) {
                        emit("Coach, I'm having trouble processing that. Let's try a different tactical approach - could you rephrase your question?")
                    }
                }
            }

            if (!hasEmittedContent) {
                emit("Coach, I couldn't generate a response to that specific question. Could you provide more details about the situation you're facing? The more context you give me, the better tactical advice I can provide!")
            }

        } catch (e: Exception) {
            Log.e("GeminiService", "Error in sendMessage: ${e.message}", e)

            val errorMessage = when {
                e.message?.contains("QUOTA", ignoreCase = true) == true ||
                        e.message?.contains("quota", ignoreCase = true) == true -> {
                    Log.e("GeminiService", "Quota exceeded - Coach Pep taking a tactical timeout...")
                    "Coach, we've hit our API limit for now. Even the best teams need tactical timeouts! Please wait a few minutes and we'll be back with full coaching power."
                }
                e.message?.contains("RESOURCE_EXHAUSTED", ignoreCase = true) == true -> {
                    "Coach, our system is at capacity right now. Like managing squad rotation, we need to give our resources a brief rest. Please try again in a few minutes!"
                }
                e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true -> {
                    "Coach, there's an authentication issue with our system. Please check that everything is set up correctly, just like checking your team lineup before kickoff!"
                }
                e.message?.contains("INVALID_ARGUMENT", ignoreCase = true) == true -> {
                    "Coach, there seems to be an issue with how that request was formatted. Let's try rephrasing your question - sometimes a different tactical approach works better!"
                }
                e.message?.contains("UNAVAILABLE", ignoreCase = true) == true -> {
                    "Coach, our AI coaching system is temporarily unavailable. Even the best teams face technical difficulties! Please try again in a few minutes."
                }
                else -> {
                    "Coach, we've encountered a technical challenge: ${e.message ?: "Unknown error"}. Every setback is a setup for a comeback - let's try again!"
                }
            }

            emit(errorMessage)
        }
    }
        .catch { e ->
            Log.e("GeminiService", "Flow error: ${e.message}", e)
            emit("Coach, we're experiencing connection issues. Just like in football, communication is key! Please check your internet connection and let's get back to tactical planning.")
        }
        .flowOn(Dispatchers.IO)
}
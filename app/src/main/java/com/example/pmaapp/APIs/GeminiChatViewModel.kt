package com.example.pmaapp.APIs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmaapp.services.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiChatViewModel : ViewModel() {

    data class ChatMessage(
        val text: String,
        val isUserMessage: Boolean,
        val isError: Boolean = false,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val geminiService = GeminiService()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Enhanced context prompt for Coach Pep persona
    private val coachPepContext = """
        Act like a world-class football coach integrated into a mobile app. Your identity is based on "Pep Guardiola," one of the best football coaches in history, known for his tactical genius, creativity, professionalism, leadership, and over 15 years of experience managing top clubs like Barcelona, Bayern Munich, and Manchester City.

        You are a virtual assistant named "Coach Pep" inside the app. You are designed to help real-life football coaches (at amateur and professional levels) make smart decisions during matches and training sessions, always strictly following FIFA Football Laws and best professional practices.

        **Objective:**
        Your goal is to assist coaches by providing accurate, detailed, and strategic advice. You must help them:
        • Make live tactical decisions (formation changes, substitutions, set-piece tactics)
        • Plan training sessions (technical, tactical, physical, and psychological aspects)
        • Manage player development, injuries, and fitness
        • Create game strategies based on opponents' strengths and weaknesses
        • Ensure that all advice complies with FIFA rules and standards
        • Encourage creativity, teamwork, and player development at all times

        **Rules you must follow:**
        1. Always act like a professional with a minimum of 15 years of top-level coaching experience
        2. Emphasize tactical intelligence, strategic creativity, and leadership
        3. Strictly follow and reference official FIFA Football Laws when applicable
        4. When giving advice, explain your reasoning clearly in 3 detailed paragraphs
        5. Always present two or more alternative options when offering tactical solutions
        6. If uncertain about a decision due to lack of information, ask precise clarifying questions to the coach
        7. Use professional coaching language, avoid slang, and maintain a positive, motivating tone
        8. Always prioritize player safety, fair play, and long-term player development
        9. Provide recommendations in a structured format: [Situation Analysis] → [Options] → [Recommended Action]
        10. Never invent FIFA rules. If unsure, state, "Please consult the latest FIFA rulebook for verification."

        **Step-by-Step Approach:**
        1. Analyze the coach's situation carefully and identify the core football problem
        2. Cross-reference advice with FIFA Football Laws when necessary
        3. List all possible tactical/strategic options
        4. Recommend the best course of action with detailed justification
        5. Provide additional proactive tips that a top coach would consider
        6. Offer a motivational, professional closing statement to support the coach's confidence

        **Important Formatting Guidelines:**
        • Use bullet points for options when applicable
        • Use bold headings like **Situation Analysis**, **Tactical Options**, **Recommended Action**, **Pro Tip**
        • Use short but powerful motivational phrases like "Stay focused!", "Adapt and conquer!", "Trust your vision!" after major advice

        Remember: Take a deep breath and work on this problem step-by-step.
    """.trimIndent()

    /** Initialize chat with Coach Pep persona */
    fun initializeChat(additionalContext: String? = null) {
        viewModelScope.launch {
            try {
                val fullContext = if (additionalContext.isNullOrBlank()) {
                    coachPepContext
                } else {
                    "$coachPepContext\n\nAdditional Context: $additionalContext"
                }

                withContext(Dispatchers.Main) {
                    _chatMessages.value = listOf(
                        ChatMessage(
                            text = "Hello, Coach! I'm Coach Pep, your virtual football assistant. With over 15 years of top-level coaching experience, I'm here to help you with tactical decisions, training plans, player development, and match strategies.\n\nWhether you need help with formations, substitutions, set-pieces, or building your team's playing philosophy, I'll provide you with professional insights following FIFA standards.\n\nWhat football challenge can I help you tackle today? 🏈⚽",
                            isUserMessage = false
                        )
                    )
                }

                Log.d("GeminiChatViewModel", "Chat initialized with Coach Pep context")
            } catch (e: Exception) {
                Log.e("GeminiChatViewModel", "Error initializing chat", e)

                withContext(Dispatchers.Main) {
                    _chatMessages.value = listOf(
                        ChatMessage(
                            text = "Hello, Coach! I'm Coach Pep, your virtual football assistant. How can I help you with your football tactics and strategies today?",
                            isUserMessage = false
                        )
                    )
                }
            }
        }
    }

    /** Send a user message, stream and display the AI response */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // Immediately show the user's own message
        val userMessage = ChatMessage(message.trim(), true)
        addMessage(userMessage)

        viewModelScope.launch {
            try {
                _isLoading.value = true
                var hasReceivedResponse = false

                geminiService
                    .sendMessage(message.trim(), coachPepContext)
                    .catch { exception ->
                        Log.e("GeminiChatViewModel", "Error in sendMessage flow", exception)
                        if (exception !is CancellationException) {
                            val errorMsg = when {
                                exception.message?.contains("network", ignoreCase = true) == true ->
                                    "Network error, Coach. Please check your connection and try again. Even the best tactics need a solid connection!"
                                exception.message?.contains("timeout", ignoreCase = true) == true ->
                                    "Request timed out, Coach. Sometimes we need to take a tactical pause. Please try again."
                                else ->
                                    "Sorry Coach, I encountered a technical issue. Let's regroup and try that again!"
                            }
                            addOrUpdateAIMessage(errorMsg, isError = true)
                        }
                    }
                    .collect { partial ->
                        hasReceivedResponse = true
                        addOrUpdateAIMessage(partial)
                    }

                // If no response was received, add a fallback message
                if (!hasReceivedResponse) {
                    addOrUpdateAIMessage(
                        "Coach, I'm having trouble processing that request right now. Let's try a different approach - could you rephrase your question?",
                        isError = true
                    )
                }

            } catch (e: Exception) {
                Log.e("GeminiChatViewModel", "Error in sendMessage coroutine", e)
                if (e !is CancellationException) {
                    val errorMessage = when {
                        e.message?.contains("network", ignoreCase = true) == true ->
                            "Network connection failed, Coach. Even the best game plans need good communication. Please check your internet and try again."
                        e.message?.contains("api", ignoreCase = true) == true ->
                            "Technical timeout, Coach. Let's take a brief tactical pause and try again in a moment."
                        else ->
                            "Something went wrong, Coach. Every setback is a setup for a comeback. Please try again!"
                    }
                    addOrUpdateAIMessage(errorMessage, isError = true)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        try {
            val currentMessages = _chatMessages.value.toMutableList()
            currentMessages.add(message)
            _chatMessages.value = currentMessages
        } catch (e: Exception) {
            Log.e("GeminiChatViewModel", "Error adding message", e)
        }
    }

    private fun addOrUpdateAIMessage(text: String, isError: Boolean = false) {
        try {
            val msgs = _chatMessages.value.toMutableList()

            if (msgs.isEmpty()) {
                msgs.add(ChatMessage(text, false, isError))
            } else {
                val lastIndex = msgs.lastIndex
                val lastMessage = msgs[lastIndex]

                if (!lastMessage.isUserMessage) {
                    // Update existing AI message
                    msgs[lastIndex] = ChatMessage(text, false, isError)
                } else {
                    // Add new AI message
                    msgs.add(ChatMessage(text, false, isError))
                }
            }

            _chatMessages.value = msgs
        } catch (e: Exception) {
            Log.e("GeminiChatViewModel", "Error updating AI message", e)
            // Try to add a basic error message as fallback
            try {
                val msgs = _chatMessages.value.toMutableList()
                msgs.add(ChatMessage("Coach, there was an error displaying the response. Let's try again!", false, true))
                _chatMessages.value = msgs
            } catch (fallbackError: Exception) {
                Log.e("GeminiChatViewModel", "Even fallback failed", fallbackError)
            }
        }
    }

    /** Clear everything and reinitialize with Coach Pep context */
    fun resetChat() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    _chatMessages.value = emptyList()
                    _isLoading.value = false
                }

                // Reinitialize with Coach Pep greeting
                initializeChat()

                Log.d("GeminiChatViewModel", "Chat reset and reinitialized with Coach Pep")
            } catch (e: Exception) {
                Log.e("GeminiChatViewModel", "Error resetting chat", e)
                // Force reset even if there's an error
                try {
                    _chatMessages.value = emptyList()
                    _isLoading.value = false
                } catch (forceError: Exception) {
                    Log.e("GeminiChatViewModel", "Force reset also failed", forceError)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GeminiChatViewModel", "Coach Pep ViewModel cleared")
    }
}
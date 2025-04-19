package com.example.pmaapp.APIs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmaapp.services.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GeminiChatViewModel : ViewModel() {

    data class ChatMessage(
        val text: String,
        val isUserMessage: Boolean
    )

    private val geminiService = GeminiService()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var contextPrompt: String? = null

    /** Set the one‑time context string (formerly “system” role) */
    fun initializeChat(initialContext: String) {
        contextPrompt = initialContext.trim().takeIf { it.isNotBlank() }
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Hello! I'm your football assistant. How can I help you today?",
                isUserMessage = false
            )
        )
    }

    /** Send a user message, stream and display the AI response */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // Immediately show the user’s own message
        _chatMessages.value = _chatMessages.value + ChatMessage(message, true)
        _isLoading.value = true

        viewModelScope.launch {
            var lastFragment = ""
            geminiService
                .sendMessage(message, contextPrompt)
                .collect { partial ->
                    lastFragment = partial

                    // Replace or append the AI message
                    val msgs = _chatMessages.value.toMutableList()
                    val aiIndex = msgs.indexOfLast { !it.isUserMessage }
                    if (aiIndex >= 0 && msgs.lastIndex == aiIndex) {
                        // update existing AI bubble
                        msgs[aiIndex] = ChatMessage(partial, false)
                    } else {
                        // add new AI bubble
                        msgs.add(ChatMessage(partial, false))
                    }
                    _chatMessages.value = msgs
                }

            _isLoading.value = false
        }
    }

    /** Clear everything (including context if you want) */
    fun resetChat(clearContext: Boolean = false) {
        if (clearContext) contextPrompt = null
        _chatMessages.value = emptyList()
        _isLoading.value = false
    }
}

package com.example.pmaapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pmaapp.APIs.GeminiChatViewModel
import com.example.pmaapp.ui.theme.PMAAppTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(
    navController: NavController,
    initialPrompt: String? = null,
    viewModel: GeminiChatViewModel = viewModel()
) {
    // Initialize Coach Pep with any additional context
    LaunchedEffect(Unit) {
        try {
            viewModel.initializeChat(initialPrompt)
            Log.d("GeminiChatScreen", "Coach Pep initialized successfully")
        } catch (e: Exception) {
            Log.e("GeminiChatScreen", "Error initializing Coach Pep", e)
        }
    }

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val chatListState = rememberLazyListState()
    var userInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        try {
            if (chatMessages.isNotEmpty()) {
                // Add a small delay to ensure the message is fully rendered
                delay(100)
                chatListState.animateScrollToItem(chatMessages.size - 1)
            }
        } catch (e: Exception) {
            Log.e("GeminiChatScreen", "Error scrolling to bottom", e)
        }
    }

    // Function to send message with proper validation
    val sendMessage: (String) -> Unit = remember {
        { message: String ->
            if (message.isNotBlank() && !isLoading) {
                try {
                    viewModel.sendMessage(message.trim())
                    userInput = ""
                    keyboardController?.hide()
                } catch (e: Exception) {
                    Log.e("GeminiChatScreen", "Error sending message", e)
                }
            }
        }
    }

    // Function to reset chat
    val resetChat: () -> Unit = remember {
        {
            try {
                viewModel.resetChat()
            } catch (e: Exception) {
                Log.e("GeminiChatScreen", "Error resetting chat", e)
            }
        }
    }

    PMAAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Coach Pep",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Your Virtual Football Coach",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                try {
                                    navController.navigateUp()
                                } catch (e: Exception) {
                                    Log.e("GeminiChatScreen", "Error navigating back", e)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = resetChat,
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset chat",
                                tint = if (!isLoading)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Chat messages display area
                    LazyColumn(
                        state = chatListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(
                            items = chatMessages,
                            key = { message -> "${message.timestamp}-${message.isUserMessage}" }
                        ) { message ->
                            ChatMessageItem(
                                message = message,
                                isUserMessage = message.isUserMessage
                            )
                        }

                        // Show loading indicator when generating response
                        if (isLoading) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Coach Pep is analyzing...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Quick suggestion chips for common coaching scenarios
                    if (chatMessages.size <= 1 && !isLoading) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(quickSuggestions) { suggestion ->
                                SuggestionChip(
                                    onClick = { sendMessage(suggestion) },
                                    label = {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    // Input area
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Text input field
                            OutlinedTextField(
                                value = userInput,
                                onValueChange = { newValue ->
                                    userInput = newValue
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                placeholder = {
                                    Text(
                                        text = if (isLoading) "Coach Pep is thinking..." else "Ask Coach Pep about tactics, training, or strategy...",
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = if (isLoading) 0.5f else 0.7f
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(24.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        sendMessage(userInput)
                                    }
                                ),
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                ),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            sendMessage(userInput)
                                        },
                                        enabled = !isLoading && userInput.isNotBlank()
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send message",
                                            tint = if (!isLoading && userInput.isNotBlank())
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: GeminiChatViewModel.ChatMessage,
    isUserMessage: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUserMessage) 16.dp else 4.dp,
                bottomEnd = if (isUserMessage) 4.dp else 16.dp
            ),
            color = when {
                isUserMessage -> MaterialTheme.colorScheme.primaryContainer
                message.isError -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            },
            tonalElevation = if (message.isError) 4.dp else 2.dp,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(
                    end = if (isUserMessage) 0.dp else 60.dp,
                    start = if (isUserMessage) 60.dp else 0.dp
                )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Add coach icon for AI messages
                if (!isUserMessage && !message.isError) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "⚽",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Coach Pep",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = when {
                        isUserMessage -> MaterialTheme.colorScheme.onPrimaryContainer
                        message.isError -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }
        }
    }
}

// Quick suggestions for common coaching scenarios
private val quickSuggestions = listOf(
    "Help with 4-3-3 formation",
    "Defending set pieces",
    "Player development tips",
    "Halftime tactical changes",
    "Training session plan"
)
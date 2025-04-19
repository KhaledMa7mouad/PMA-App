package com.example.pmaapp.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pmaapp.APIs.GeminiChatViewModel
import com.example.pmaapp.ui.theme.PMAAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(
    navController: NavController,
    initialPrompt: String = "Help me with football player stats and tactics",
    viewModel: GeminiChatViewModel = viewModel()
) {
    // Initialize with the specified context
    LaunchedEffect(initialPrompt) {
        viewModel.initializeChat(initialPrompt)
    }

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val chatListState = rememberLazyListState()
    var userInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    PMAAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "AI Assistant",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back"
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
                        items(chatMessages) { message ->
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
                                        strokeWidth = 2.dp
                                    )
                                }
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
                                onValueChange = { userInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                placeholder = { Text("Type a message...") },
                                shape = RoundedCornerShape(24.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (userInput.isNotBlank()) {
                                            viewModel.sendMessage(userInput)
                                            userInput = ""
                                            keyboardController?.hide()
                                        }
                                    }
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                ),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (userInput.isNotBlank()) {
                                                viewModel.sendMessage(userInput)
                                                userInput = ""
                                                keyboardController?.hide()
                                            }
                                        },
                                        enabled = !isLoading && userInput.isNotBlank()
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send message",
                                            tint = if (!isLoading && userInput.isNotBlank())
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
            color = if (isUserMessage)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(end = if (isUserMessage) 0.dp else 60.dp, start = if (isUserMessage) 60.dp else 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUserMessage)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
package com.mujapps.mchatbot.presentation.views

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Data class to hold a single message in the chat
data class Message(
    val chatId: String,
    val text: String,
    val image: Bitmap? = null,
    val isFromUser: Boolean
)

// Data class to hold the overall UI state
data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    @OptIn(ExperimentalUuidApi::class)
    fun generateContent(prompt: String, image: Bitmap? = null) {
        val userMessage = Message(
            chatId = Uuid.random().toString(),
            text = prompt,
            image = image,
            isFromUser = true
        )

        // Add user's message to the list and show loading
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val inputContent = content {
                    image?.let { image(it) }
                    text(prompt)
                }

                //*******************************************************************
                //Wait for full response receive and update at once
                /*val response = generativeModel.generateContent(inputContent)
                response.text?.let {
                    val modelMessage =
                        Message(chatId = Uuid.random().toString(), it, isFromUser = false)
                    // Add model's response and stop loading
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + modelMessage,
                        isLoading = false
                    )*/
                //*******************************************************************
                //Using streams to get replies as a text flow without waiting until full response to Update UI
                var fullResponse = ""
                var modelMessage: Message
                var isFirstChunk = true
                val messageChatID = Uuid.random().toString()
                generativeModel.generateContentStream(inputContent).collect {
                    fullResponse += it.text
                    modelMessage =
                        Message(chatId = messageChatID, fullResponse, isFromUser = false)
                    // Add model's response and stop loading
                    if (isFirstChunk) {
                        _uiState.value = _uiState.value.copy(
                            messages = _uiState.value.messages + modelMessage,
                            isLoading = false
                        )
                        isFirstChunk = false
                    } else {
                        val updatedMessages = _uiState.value.messages.toMutableList().apply {
                            set(lastIndex, last().copy(text = fullResponse))
                        }
                        _uiState.value = _uiState.value.copy(
                            messages = updatedMessages
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error and stop loading
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun errorShown() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

package com.mujapps.mchatbot.presentation.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.VolumeMute
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mujapps.mchatbot.presentation.views.ChatUiState
import com.mujapps.mchatbot.presentation.views.MainViewModel
import com.mujapps.mchatbot.presentation.views.Message

@Composable
fun HomeScreen(
    mMainViewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val uiState by mMainViewModel.uiState.collectAsState()
    var mPromptText by remember { mutableStateOf("") }
    var mSelectedImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                mSelectedImage = ImageDecoder.decodeBitmap(source)
            }
        }
    )

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val spokenText =
                    data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                        results[0]
                    }
                if (spokenText != null && spokenText.isNotBlank()) {
                    mMainViewModel.generateContent(spokenText)
                }
            }
        }
    )

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            mMainViewModel.errorShown()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ChatScreen(modifier = Modifier.weight(1f), uiState = uiState)

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B4513))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    TextField(
                        value = mPromptText,
                        onValueChange = { newText -> mPromptText = newText },
                        label = {
                            Text("Enter the prompt please..")
                        },
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = "Attach Image",
                        tint = Color.Black
                    )
                }

                Button(
                    onClick = {
                        speechRecognizerLauncher.launch(voiceIntent)
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.VolumeMute,
                        contentDescription = "Attach Voice",
                        tint = Color.Black
                    )
                }

                Button(
                    onClick = {
                        if (mSelectedImage == null) {
                            if (mPromptText.isNotBlank()) {
                                mMainViewModel.generateContent(mPromptText, null)
                                mPromptText = ""
                                mSelectedImage = null
                            }
                        } else {
                            if (mPromptText.isNotBlank()) {
                                mMainViewModel.generateContent(mPromptText, mSelectedImage)
                            } else {
                                mMainViewModel.generateContent(
                                    "Please explain this image",
                                    mSelectedImage
                                )
                            }
                            mPromptText = ""
                            mSelectedImage = null
                        }
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun ChatScreen(modifier: Modifier = Modifier, uiState: ChatUiState) {
    LazyColumn(modifier = modifier.fillMaxWidth(), reverseLayout = true) {
        items(uiState.messages.reversed()) { message ->
            ChatBubble(message = message)
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val bubbleColor =
        if (message.isFromUser) Color.Yellow.copy(alpha = 0.4f) else Color.Green.copy(alpha = 0.4f)
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .align(alignment),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = if (message.isFromUser) 0.dp else 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 0.dp
            ),
            color = bubbleColor
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                message.image?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = message.text, style = TextStyle(color = Color.White),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

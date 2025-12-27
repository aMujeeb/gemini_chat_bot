Gemini ChatBot
A modern Android chatbot application built with Jetpack Compose and the Google Gemini API. This project serves as a comprehensive example of how to integrate cutting-edge generative AI into a native Android app using the latest tools and best practices.
The app provides a clean, responsive, chat-like interface that supports text, image, and voice-based prompts, with real-time streaming for the model's responses.
✨ Features
•
🤖 Gemini Pro Integration: Connects to the gemini-2.5-flash model via the Firebase AI SDK.
•
💬 Real-time Streaming: Responses are streamed chunk-by-chunk for a smooth, real-time chat experience.
•
🖼️ Multimodal Input: Supports both text and image prompts. You can ask questions about images from your gallery.
•
🎤 Voice-to-Text: Use your voice to dictate prompts. The app automatically recognizes the speech and sends it to the model.
•
Modern UI: A clean, chat-bubble interface built entirely with Jetpack Compose and Material 3 components.
•
State-of-the-Art Architecture: Follows modern Android architecture (MVVM) using ViewModel and StateFlow for robust state management.
🛠️ Tech Stack & Architecture
•
Core Language: Kotlin
•
UI: Jetpack Compose & Material 3
•
Architecture: MVVM (Model-View-ViewModel)
•
Asynchronous: Kotlin Coroutines & Flow
•
AI: Firebase AI SDK for Gemini
•
State Management: ViewModel and StateFlow
•
Permissions: Handles runtime permissions for audio recording.

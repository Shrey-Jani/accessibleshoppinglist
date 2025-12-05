# Accessible Shopping List

**Team Members**:
- Aditya Dave (991668091)
- Shrey Jani (991668104)
- Yagna Patel (991665691)

## Project Overview
Accessible Shopping List is an Android application designed to make shopping list management easy for everyone, with a specific focus on accessibility. It features high-contrast modes, large text options, one-handed operation layouts, and voice input to assist users with visual or motor impairments.

## Key Features
- **Authentication**: Secure Login, Register, and Forgot Password flows using Firebase Authentication.
- **Real-time Database**: Shopping lists are synced in real-time using Cloud Firestore.
- **Voice Input**: Add items simply by speaking, powered by Android's SpeechRecognizer.
- **Accessibility Settings**:
- **High Contrast Mode**: Switches to a high-visibility black/white/yellow theme.
- **Large Text Mode**: Increases text size for better readability.
- **One-Handed Mode**: Moves interactive elements to the bottom of the screen for easier reach.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Repository Pattern
- **Backend**: Firebase Auth & Firestore
- **Local Storage**: DataStore (for Settings)

## Setup Instructions
1.  Clone the repository.
2.  Open in Android Studio Ladybug (or newer).
3.  Sync Gradle.
4.  Run on an Emulator (Recommended: API 35 Medium Phone).

## How to Test Advanced Features
1.  **Voice Input**: Go to "Add Item", tap the Mic icon, and speak the item name.
2.  **High Contrast**: Go to Settings -> Enable "High-contrast mode".
3.  **One-Handed Mode**: Go to Settings -> Enable "One-hand mode" -> Go back to List -> Verify buttons are at the bottom.



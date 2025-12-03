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



# Running the App on a Physical Android Device (for Microphone / Speech-to-Text)

The Android emulator often does **not** support real microphone input.  
To properly test the voice input (speech-to-text) feature, you **must** run the app on a physical Android device.

---

## 1. Enable Developer Options on the Phone

1. Open **Settings** on your Android device.
2. Scroll down and tap **About phone**.
3. Find one of the following (depends on device/brand):
   - **Build number**, or  
   - **Software information → Build number**, or  
   - On Xiaomi / Redmi / POCO: **MIUI version**
4. Tap **Build number** (or **MIUI version**) **7 times** until you see a message like:
   > You are now a developer.

Developer options are now enabled.

---

## 2. Enable USB Debugging

1. Open **Settings** again.
2. Go to:
   - **System → Developer options**, or  
   - On some phones: **Settings → Developer options** directly.
3. Scroll down and find **USB debugging**.
4. Turn **USB debugging** to **ON**.
5. Confirm any pop-ups asking to allow USB debugging.

---

## 3. If You See “USB debugging blocked by AutoBlocker”

On some devices (especially Samsung), you may see:

> USB debugging blocked by AutoBlocker

To allow debugging:

1. Open **Settings** on the phone.
2. Go to **Security and privacy**.
3. Tap **Auto Blocker**.
4. Turn **Auto Blocker** **OFF**, or disable any option that says:
   - **Block USB debugging**  
   - or similar wording
5. Disconnect and reconnect the USB cable after changing this setting.

When you reconnect, you should see:

> Allow USB debugging from this computer?  
> [Allow]

Tap **Allow** (optionally check **Always allow from this computer**).

---

## 4. Connect the Phone to the Mac

Use one of the following:

- **USB-C to USB-C** cable (most modern phones and MacBooks)
- **USB-A to USB-C** cable with a **USB-C adapter** on the Mac
- The regular charging cable for the phone (if it supports data transfer)

Then:

1. Plug the phone into the Mac.
2. Unlock the phone screen.
3. Confirm the **“Allow USB debugging”** prompt on the phone.

---

## 5. Select the Device in Android Studio

1. Open **Android Studio**.
2. At the top of the window, locate the **device selector** (where the emulator name appears).
3. Your phone should appear in the list (for example: `SM-A546B`, `Pixel 6`, etc.).
4. Select the physical device.
5. Click **Run ▶** to build and install the app on the phone.

If the phone does not appear:
- Try another USB cable.
- Restart Android Studio.
- Disconnect and reconnect the cable.
- Make sure **USB debugging** is still enabled.
- Make sure **Auto Blocker** is disabled (see section 3).

---

## 6. Test the Microphone / Speech-to-Text Feature

Once the app is running on the physical device:

1. Navigate to the **Add Item / Edit Item** screen (where the item name is entered).
2. Tap the **microphone icon** in the UI.
3. When the system speech dialog appears, speak clearly into the phone.
4. The recognized text should appear in the corresponding text field (e.g., item name).

If this is done on the emulator instead of a physical device, you will likely see messages like:

> Could not understand. Please try again.

This is expected on many emulators and is a limitation of the emulator audio, not the app logic.

---

## 7. Summary

- **Emulator microphone is unreliable or unsupported** → avoid using it for testing speech.
- **Physical Android device is required** to validate voice input.
- Ensure:
  - Developer options are enabled.
  - USB debugging is turned on.
  - Auto Blocker (or similar security feature) is not blocking debugging.
  - The device is visible and selected in Android Studio.

Once these steps are followed, team members should be able to run the app on their own devices and fully test the microphone-based features.

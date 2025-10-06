# CuteChat - Modern Android Communication App

A beautiful, modern Android app built with Java, Firebase, and WebRTC for real-time messaging and video/audio calls.

## Features

### 🔐 Authentication
- Email/Password authentication
- Google Sign-In integration
- Secure user profile management

### 👥 Social Features
- Add friends by username
- Send and receive friend requests
- Accept/decline friend requests
- Real-time friend status (Online/Offline)

### 💬 Real-time Messaging
- Instant text messaging using Firebase Firestore
- Message status indicators (Sent/Delivered/Read)
- Typing indicators
- Emoji support
- Image sharing
- Timestamp display

### 📞 Audio & Video Calls
- High-quality WebRTC video calls
- Crystal clear audio calls
- Call notifications
- Call timer
- Mute/unmute functionality
- Camera switching
- Call history

### 🎨 Modern UI/UX
- Material 3 design
- Cute, minimal interface
- Smooth animations
- Gradient backgrounds
- Dark/Light theme support
- Responsive design

### 🔔 Push Notifications
- Firebase Cloud Messaging
- Incoming call notifications
- Message notifications
- Friend request notifications

## Tech Stack

- **Language**: Java
- **UI Framework**: Android Views with Material 3
- **Backend**: Firebase
  - Authentication
  - Firestore (Real-time Database)
  - Cloud Storage
  - Cloud Messaging
- **Real-time Communication**: WebRTC
- **Image Loading**: Glide
- **Architecture**: MVVM pattern

## Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Java 8+
- Firebase project setup
- Google Sign-In configuration

## Setup Instructions

### 1. Firebase Configuration

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication with Email/Password and Google Sign-In
3. Create a Firestore database
4. Enable Cloud Storage
5. Set up Cloud Messaging
6. Download `google-services.json` and replace the placeholder in `app/google-services.json`

### 2. Google Sign-In Setup

1. In Firebase Console, go to Authentication > Sign-in method
2. Enable Google Sign-In
3. Get your Web Client ID from the Google Sign-In configuration
4. Update the Web Client ID in `AuthManager.java`:
   ```java
   .requestIdToken("YOUR_ACTUAL_WEB_CLIENT_ID")
   ```

### 3. Build Configuration

1. Open the project in Android Studio
2. Sync Gradle files
3. Update the `applicationId` in `app/build.gradle` if needed
4. Build and run the project

### 4. Permissions

The app requires the following permissions:
- `CAMERA` - For video calls
- `RECORD_AUDIO` - For audio calls
- `INTERNET` - For network communication
- `ACCESS_NETWORK_STATE` - For network status
- `WAKE_LOCK` - For call notifications
- `VIBRATE` - For notifications

## Project Structure

```
app/
├── src/main/java/com/cutechat/app/
│   ├── firebase/           # Firebase configuration and managers
│   ├── models/             # Data models (User, Message, etc.)
│   ├── ui/                 # UI components
│   │   ├── auth/           # Authentication screens
│   │   ├── main/           # Main activity and fragments
│   │   ├── chat/           # Chat functionality
│   │   ├── call/           # Call functionality
│   │   ├── friends/        # Friends management
│   │   └── settings/       # Settings screen
│   ├── webrtc/             # WebRTC implementation
│   ├── services/           # Background services
│   └── receivers/          # Broadcast receivers
├── src/main/res/           # Resources
│   ├── drawable/           # Icons and drawables
│   ├── layout/             # XML layouts
│   ├── values/             # Strings, colors, themes
│   └── menu/               # Menu resources
└── google-services.json    # Firebase configuration
```

## Key Components

### Authentication System
- `AuthManager.java` - Handles all authentication operations
- `AuthActivity.java` - Login/Register UI
- Firebase Authentication integration

### Real-time Messaging
- `ChatActivity.java` - Chat interface
- `MessagesAdapter.java` - Message list adapter
- Firestore real-time listeners

### WebRTC Calls
- `CallActivity.java` - Call interface
- `WebRTCManager.java` - WebRTC implementation
- STUN server configuration

### UI Components
- Material 3 design system
- Custom themes and colors
- Responsive layouts
- Smooth animations

## Firebase Rules

### Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Messages are readable by sender and receiver
    match /messages/{messageId} {
      allow read, write: if request.auth != null && 
        (resource.data.senderId == request.auth.uid || 
         resource.data.receiverId == request.auth.uid);
    }
    
    // Friend requests
    match /friendRequests/{requestId} {
      allow read, write: if request.auth != null && 
        (resource.data.senderId == request.auth.uid || 
         resource.data.receiverId == request.auth.uid);
    }
  }
}
```

## Building APK

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/`

## Features Implementation Status

- ✅ Authentication (Email/Password, Google Sign-In)
- ✅ User Profile Management
- ✅ Friend System (Add, Accept, Reject)
- ✅ Real-time Messaging
- ✅ WebRTC Audio/Video Calls
- ✅ Push Notifications
- ✅ Modern UI/UX
- ✅ Settings and Theme
- ⚠️ Image Sharing (UI ready, backend integration needed)
- ⚠️ Emoji Picker (UI ready, implementation needed)
- ⚠️ Call History (UI ready, data persistence needed)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue on GitHub.

---

**Note**: This is a complete Android project ready for development and testing. Make sure to configure Firebase properly before running the app.
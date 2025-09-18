# WebView Android App

A modern Android WebView application with beautiful UI/UX design and smooth loading animations.

## Features

- **Modern Material Design 3 UI** - Clean and intuitive interface following Google's latest design guidelines
- **Smooth Loading Animations** - Beautiful Lottie animations during page loading
- **Navigation Controls** - Back, Forward, Refresh, and Home buttons for easy navigation
- **Progress Indicator** - Linear progress bar showing page loading progress
- **Pull-to-Refresh** - Swipe down to refresh the current page
- **Dark Theme Support** - Automatic dark/light theme switching based on system settings
- **Edge-to-Edge Display** - Modern full-screen experience
- **Responsive Design** - Optimized for all screen sizes

## Package Information

- **Package Name**: `com.oxgtech.webview`
- **Target SDK**: Android 14 (API 34)
- **Minimum SDK**: Android 7.0 (API 24)

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Material Design 3
- **Animation**: Lottie
- **WebView**: AndroidX WebKit
- **Architecture**: Single Activity with WebView

## Project Structure

```
app/
├── src/main/
│   ├── java/com/oxgtech/webview/
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml
│   │   ├── values/
│   │   │   ├── colors.xml
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   ├── values-night/
│   │   │   └── themes.xml
│   │   ├── drawable/
│   │   │   ├── ic_*.xml (navigation icons)
│   │   │   └── ic_launcher_*.xml
│   │   ├── raw/
│   │   │   └── loading_animation.json
│   │   └── mipmap-*/
│   │       └── ic_launcher.*
│   └── AndroidManifest.xml
├── build.gradle
└── proguard-rules.pro
```

## Key Components

### MainActivity
- WebView configuration with JavaScript enabled
- Loading screen management
- Navigation controls implementation
- Progress tracking
- Error handling

### UI Elements
- **Toolbar**: Material toolbar with page title
- **Progress Bar**: Linear progress indicator
- **Loading Screen**: Lottie animation with loading text
- **Bottom Navigation**: Back, Forward, Refresh, Home buttons
- **SwipeRefreshLayout**: Pull-to-refresh functionality

### Permissions
- `INTERNET` - Required for web browsing
- `ACCESS_NETWORK_STATE` - For network status checking
- `ACCESS_WIFI_STATE` - For WiFi status checking

## Building the App

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build and run on your device or emulator

## Customization

### Changing Home URL
Edit the `homeUrl` variable in `MainActivity.kt`:
```kotlin
private val homeUrl = "https://your-website.com"
```

### Modifying Colors
Update colors in `res/values/colors.xml` and `res/values-night/colors.xml`

### Custom Loading Animation
Replace `res/raw/loading_animation.json` with your own Lottie animation

## Requirements

- Android Studio Arctic Fox or later
- Android SDK 34
- Kotlin 1.9.10 or later
- Gradle 8.2.0 or later

## License

This project is open source and available under the MIT License.
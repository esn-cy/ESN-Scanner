<h1 align="center">
    <img src="./androidApp/src/main/ic_launcher-playstore.png" height="200" alt="ESN Scanner Logo"/><br>
    ESN Scanner
</h1>

ESN Scanner is a Kotlin Multiplatform (KMP) application designed to validate ESNcards and event Free
Passes efficiently.
Built with modern mobile technologies, it ensures a smooth experience for ESN volunteers and staff
on both Android and iOS devices.
This flexibility makes it highly adaptable for various events and entry points.

## Features

- **📷 Universal Scanning:** Quickly scan both Barcodes (ESNcards) and QR codes (Free Passes) to
  verify validity.
- **🎟️ Instant Validation:** Receive immediate feedback on membership status or pass validity.
- **🌗 Dark & Light Mode:** Seamless UI adapted for any lighting condition, perfect for night events.
- **🔗 Cross-Platform:** A single codebase powering native experiences on both Android and iOS.
- **⚡ Offline First:** Core scanning functionality works rapidly, with background syncing for
  updates.
- **📊 Analytics Integrated:** Uses Firebase to track usage patterns and app performance.

## Setup and Configuration

Refer to the Firebase console for project settings.

**Key Setup Points:**

- **Firebase Configuration:**
    * **Android:** Ensure `google-services.json` is present in the `androidApp/` directory.
    * **iOS:** Ensure `GoogleService-Info.plist` is properly configured in the iOS project via
      Xcode.
- **Environment:**
    * **Android:** Requires Android Studio (Koala or newer recommended).
    * **iOS:** Requires a Mac with Xcode and CocoaPods installed.

## Installation & Running

ESN Scanner is a mobile application built with Gradle.

1. **Dependencies:** Ensure you have the necessary tools installed.
    * **Java/Kotlin:** JDK 17+ is recommended.
    * **CocoaPods:** Run `sudo gem install cocoapods` if missing.

2. **Build (Android):**
   ```bash
   ./gradlew installDebug
   ```
   Or simply run from Android Studio.

3. **Build (iOS):**
    1. Navigate to `iosApp/`: `cd iosApp`
    2. Install pods: `pod install`
    3. Open `iosApp.xcworkspace` in Xcode and hit Run.

## App Flow

1. **Launch App:** Open the ESN Scanner on your device.
2. **Select Mode:** Select the operation you want to perform.
3. **Point & Scan:** Align the camera with an ESNcard barcode or a Free Pass QR code.
4. **View Result:**
    * **Valid:** Green indicator with member details.
    * **Inconsistent** Yellow indicator with member details (wrong information highlighted)
    * **Expired/Invalid:** Red indicator with error reason.

## Dependencies

- **Runtime**:
    - **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html):** Core logic.
    - **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/):** UI
      framework.
- **Scanning**:
    - **[Google MLKit](https://developers.google.com/ml-kit):** On-device barcode scanning.
- **Backend & Services**:
    - **[Firebase](https://firebase.google.com/):** Crashlytics, Analytics, Performance.
    - **[Ktor](https://ktor.io/):** Networking.

## License

This project is under the [Apache 2.0](./LICENSE) Licence.
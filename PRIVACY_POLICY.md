# Privacy Policy for ESN Scanner

**Effective Date:** December 26, 2025

## 1. Introduction

ESN Cyprus ("we," "our," or "us") operates the **ESN Scanner** mobile application (the "App"). This
Privacy Policy explains how we collect, use, disclosure, and safeguard your information when you use
our App.

By using the App, you agree to the collection and use of information in accordance with this policy.

## 2. Information We Collect

### 2.1 Camera and Image Data

The App requests access to your device's camera and photo library.

* **Camera:** Used solely for scanning ESNcard barcodes and QR codes in real-time.
* **Photo Library:** Used to allow you to select an image of a barcode for scanning.
  **We do not store images captured by the camera or selected from the library on our servers.** The
  image processing to extract the barcode number happens locally on your device using Google ML Kit.

### 2.2 Scanned Data

When you scan a card, the App collects the alphanumeric code (ESNcard number) from the barcode. This
code is transmitted to external servers (see Section 3) to verify the card's validity and retrieve
associated information (such as card status and expiration).

### 2.3 Configuration Data

The App stores configuration settings locally on your device, including:

* Local section domain preferences.
* Google Spreadsheet IDs (if configured for custom dataset lookups).
  This data is stored securely on your device and is not transmitted to us, although it is used to
  construct network requests to the configured endpoints.

### 2.4 Usage and Device Information

We use third-party services like **Google Firebase** to collect standard usage and device
information to help us improve the App. This may include:

* Device model and operating system version.
* App crash reports and performance logs.
* Anonymized interaction data.

## 3. How We Use Your Information

We use the collected information for the following purposes:

* **Verification:** To check the validity of an ESNcard against ESN International, ESN Cyprus, or
  your section's specific database.
* **Functionality:** To display relevant cardholder information (e.g., status, expiration date)
  retrieved from the verification source.
* **Improvement:** To monitor App stability and performance using crash reporting and analytics.

## 4. Third-Party Services and Data Sharing

We may share data with specific third-party service providers to facilitate App functionality:

* **Google Firebase (Analytics, Crashlytics, Performance):** Used for collecting crash reports and
  usage metrics. [Google Privacy Policy](https://policies.google.com/privacy)
* **Google ML Kit:** Used for on-device barcode scanning.
* **ESN International (esncard.org):** If verifying international cards, the scanned card number is
  sent to ESN International's public API.
* **Google Sheets API:** If you configure a Spreadsheet ID, scanned numbers are sent to the Google
  Sheets API to fetch data from your specified sheet.
* **Custom Endpoints:** If you configure a local section domain, data is sent to that specific
  domain.

We do not sell your personal data to third parties.

## 5. Security

We strive to use commercially acceptable means to protect your information. All network
communication between the App and verification servers is conducted over secure HTTPS connections.
However, no method of transmission over the internet or method of electronic storage is 100% secure.

## 6. Children's Privacy

Our App is not intended for use by children under the age of 13. We do not knowingly collect
personally identifiable information from children under 13.

## 7. Changes to This Privacy Policy

We may update our Privacy Policy from time to time. We will notify you of any changes by posting the
new Privacy Policy on this page. You are advised to review this Privacy Policy periodically for any
changes.

## 8. Contact Us

If you have any questions about this Privacy Policy, please contact us:

* **By email:** wpa@esncy.org
* **By visiting our website:** https://esncy.org

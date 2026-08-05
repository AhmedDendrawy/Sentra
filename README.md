# Sentra 📹

A native Android surveillance and event-logging application designed for real-time camera monitoring and security alerts. Developed as a university graduation project.

## 📱 About The Project

Sentra is a robust mobile application built to provide seamless surveillance capabilities. It leverages continuous network integration and background polling mechanisms to handle real-time event logging. When a critical event is detected, the app instantly alerts the user through push notifications.

### ✨ Key Features

* **Real-Time Monitoring:** Continuous network integration for live event tracking.
* **Instant Notifications:** Integrated with Firebase Cloud Messaging (FCM) to deliver immediate alerts.
* **Background Processing:** Utilizes background polling mechanisms for uninterrupted state updates.
* **Clean UI:** Responsive and intuitive user interface built with standard Android XML layouts.

## 🛠 Tech Stack & Architecture

This project strictly follows modern Android development practices and clean code guidelines.

* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel) for clear separation of concerns and testability.
* **UI:** XML Layouts
* **Backend/Services:** Firebase (Cloud Messaging / Notifications)
* **Asynchronous Programming:** Kotlin Coroutines & Flows

## 🏗 Architecture Overview

The app is structured using the **MVVM** pattern:
* **View:** Handles UI logic and observes data changes from the ViewModel.
* **ViewModel:** Manages UI-related data in a lifecycle-conscious way and handles communication with the repositories.
* **Model/Repository:** Manages data operations, background polling, and Firebase integrations.

## 📸 Screenshots

| Splash Screen | Onboarding  | Onboarding  | Onboarding |
| :---: | :---: | :---: | :---: |
| <img width="200" alt="splash" src="https://github.com/user-attachments/assets/2963a846-4dec-4e39-b87d-be8dbc9dbb25" /> | <img width="200" alt="Screenshot_20260219_192513" src="https://github.com/user-attachments/assets/2c5c71e5-3bf7-4887-a6bf-f1598e037d23" /> | <img width="200" alt="Screenshot_20260219_192506" src="https://github.com/user-attachments/assets/f9fe90de-5da5-4e79-8f4e-2de619e2dca6" /> | <img width="200" alt="Screenshot_20260219_192420" src="https://github.com/user-attachments/assets/5ac4fe74-b5c9-4377-a394-762384c6b78e" /> |

| Login | Home | Alerts | Live Stream |
| :---: | :---: | :---: | :---: |
| <img width="200" alt="login" src="https://github.com/user-attachments/assets/10953170-b893-45ed-b10a-3d53c59b3177" /> | <img width="200" alt="home" src="https://github.com/user-attachments/assets/55c25fcd-0570-47de-acab-90cf37cb4737" /> | <img width="200" alt="alerts" src="https://github.com/user-attachments/assets/7f8b3491-9da6-4724-a82e-eaf1b96ca006" /> | <img width="200" alt="liveStream" src="https://github.com/user-attachments/assets/14114538-0477-4dd8-a528-df49a17f15ef" /> |

## 👨‍💻 Developer
Developed by **Ahmed Dandrawy Sleem** as a BSc Computer Science Graduation Project.

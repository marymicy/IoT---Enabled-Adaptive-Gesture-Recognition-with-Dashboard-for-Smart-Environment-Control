# IoT-Enabled Adaptive Gesture Recognition with Smart Environment Control

An integrated Multimodal IoT Framework designed for adaptive gesture recognition. This project combines a **high-performance Python backend** for real-time computer vision with a **native Android application**, providing a seamless spatial interface for smart environment control.

##  Overview
This system allows users to control IoT devices and system parameters (like volume and brightness) using hand gestures. It uses **MediaPipe** and **OpenCV** for precise tracking and a **Streamlit** dashboard for real-time data visualization, paired with an **Android app** for mobile-side management.

---

##  Project Structure
The repository is divided into two main components:

* **/Python-Backend**: Contains the core logic for gesture detection, adaptive filtering, and the Streamlit dashboard.
* **/GestureAssist (Android)**: The native Android Studio project providing the mobile user interface and remote control capabilities.

---

##  Key Features
* **Real-time Gesture Recognition**: Powered by MediaPipe for low-latency hand tracking.
* **Adaptive Control**: Adjusts sensitivity based on lighting and distance.
* **Cross-Platform Sync**: Synchronization between the Python processing engine and the Android mobile interface.
* **IoT Dashboard**: Visual feedback and system logs via Streamlit.

---

##  Tech Stack
* **Languages**: Python 3.x, Java (Android), XML
* **Computer Vision**: OpenCV, MediaPipe
* **Frontend/Dashboard**: Streamlit
* **Tools**: Android Studio, Git

---

## ⚙️ Installation & Setup

### Python Backend
1. Navigate to the Python directory:
   ```bash
   cd IOT-PROJ

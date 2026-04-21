# IoT - Enabled Adaptive Gesture Recognition with Dashboard Visualization for Smart Environment 

An advanced spatial interface and IoT control system that utilizes real-time hand gesture recognition to manage smart environments. This project integrates Hybrid Deep Learning with web-based dashboards to provide seamless interaction between human gestures and device control.

## 📌 Project Overview
This project addresses the need for touchless interaction in smart environments. By utilizing **MediaPipe** for landmark detection and a custom **Triple Fusion** approach (Spatial, Temporal, and Semantic), the system translates complex hand movements into hardware commands with high precision and low latency.

## 🚀 Key Features
* **Adaptive Gesture Engine**: High-accuracy hand tracking and gesture decoding using OpenCV and MediaPipe.
* **IoT Device Control**: Integrated relay logic (`gesture_relay.py`) for managing physical hardware like lights and fans.
* **Dual-Interface Dashboard**: Real-time system monitoring and feedback via Streamlit and HTML/JavaScript interfaces.
* **Native System Automation**: Hands-free control over system parameters including Volume and Brightness.

## 🛠️ Technical Stack
* **Languages**: Python 3.x, HTML5, CSS3, JavaScript
* **Computer Vision**: OpenCV, MediaPipe
* **Web Frameworks**: Streamlit (for real-time visualization)
* **Hardware Interfacing**: IoT Relay logic for Smart Environment control

## 📁 Repository Structure
* `main.py`: The primary entry point for the gesture recognition engine and processing loop.
* `gesture_relay.py`: Module handling communication between gestures and physical IoT relay states.
* `test_mp.py` & `test_cam.py`: Diagnostic suite for verifying MediaPipe performance and camera latency.
* `index.html` & `mouse_control.html`: Dashboard components for environmental monitoring.
* `distant_control.html`: Interface for remote spatial interaction.

## 🔧 Installation & Setup



1. **Clone the Repository**:
   ```bash
   git clone [https://github.com/marymicy/IoT---Enabled-Adaptive-Gesture-Recognition-with-Dashboard-for-Smart-Environment-Control.git](https://github.com/marymicy/IoT---Enabled-Adaptive-Gesture-Recognition-with-Dashboard-for-Smart-Environment-Control.git)
   cd IoT---Enabled-Adaptive-Gesture-Recognition-with-Dashboard-for-Smart-Environment-Control

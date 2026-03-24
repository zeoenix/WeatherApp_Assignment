# 🌤️ Advanced Weather Information App

![Java](https://img.shields.io/badge/Java-11%2B-orange?style=for-the-badge&logo=openjdk)
![Swing](https://img.shields.io/badge/GUI-Java_Swing-blue?style=for-the-badge&logo=java)
![OpenWeatherMap](https://img.shields.io/badge/API-OpenWeatherMap-red?style=for-the-badge&logo=openweathermap)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

A fully self-contained, enterprise-grade Java native desktop GUI application built to monitor real-time global weather conditions and 5-day forecasts. Designed with rigorous adherence to absolute zero third-party library dependencies, this application natively interacts with the OpenWeatherMap REST API while orchestrating a clean, multi-threaded User Interface.

---

## 🎯 Project Overview
This project was developed originally to satisfy the requirements of **CS 1103-01: Programming Assignment Unit 8**. The primary objective was to construct a robust piece of software capable of handling asynchronous HTTP API integration, complex JSON parsing, multi-threaded GUI concurrency, and explicit error handling—all compiled entirely from native Java.

---

## 🛠️ Technology Stack & Architecture

<p align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" alt="Java" width="45" height="45" style="margin-right: 10px;" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/bash/bash-original.svg" alt="Bash" width="45" height="45" style="margin-right: 10px;" />
</p>

- **Language**: Core Java (JDK 11+)
- **Graphical Interface**: Java Swing / AWT
- **Network / HTTP Client**: Native `java.net.http.HttpClient`
- **Data Parsing**: Highly customized Recursive-Descent Tokenizer (`SimpleJsonParser.java`) replacing external `.jar` requirements like Gson or Jackson.
- **REST APIs**: 
  - OpenWeatherMap `Weather API` (Real-time tracking)
  - OpenWeatherMap `Forecast API` (3-hour interval short-term forecasts)
  - OpenWeatherMap `Geocoding API` (Direct location searching)

---

## ✨ Core Features & Implementations

### 1. Multi-Threaded Asynchronous Architecture
To ensure the UI never freezes or hangs while waiting for heavily throttled API requests, the entire application strictly separates concerns using `SwingWorker` daemon threads. API HTTP lookups and heavy image `ImageIO.read` downloads run exclusively in the background, only joining the **AWT Event Dispatch Thread (EDT)** upon successful data return.

### 2. Intelligent Geocoding Autocomplete
Features a highly reactive `JPopupMenu` global autocomplete dropdown. As a user types a city, a `javax.swing.Timer` triggers a **500ms Debounce Mechanism**. This safely queries the OpenWeatherMap Geocoding API to predict locations across the planet without spamming or capping API quota limits.

### 3. Custom Zero-Dependency JSON Parser
Rather than polluting the academic project with massive imported library files, this software utilizes a custom-built recursive JSON tokenizer (`SimpleJsonParser.java`). It navigates complexly nested arrays and node trees received from the data satellite natively and cleanly casts them into generic mapped memory objects representing isolated POJOs (`WeatherData` & `ForecastData`).

### 4. Dynamic Time-Aware Background UI
The `DynamicBackgroundPanel` computes the active location's real-time UNIX `sunset` and `sunrise` variables obtained via JSON. Overriding the built-in Swing `paintComponent()`, the application dynamically fades the entire interface gradient from a crisp daylight Sky Blue into a deep Indigo Nighttime scheme if the local time physically surpasses dusk.

### 5. Persistent Search History State
Implements robust OS File I/O operations through the `HistoryManager`. It harnesses a `BufferedWriter` attached to a `FileWriter` inside a `try-with-resources` block to safely output geographic inquiries appended cleanly alongside literal timestamp markers inside a `search_history.txt` flat file.

---

## 📂 Project Structure
```text
WeatherInformationApp/
├── .gitignore
├── README.md
├── run.sh                          # Automated Mac/Linux bash execution script
├── run.bat                         # Automated Windows execution script
└── src/
    └── com/weatherapp/
        ├── Main.java               # Application Bootstrap & EDT initialization
        ├── model/
        │   ├── ForecastData.java   # Forecast Data POJO Encapsulation
        │   └── WeatherData.java    # Weather Data POJO Encapsulation
        ├── service/
        │   ├── HistoryManager.java # OS File I/O Reader & Writer
        │   └── WeatherService.java # HTTP Client API Endpoints Core
        ├── ui/
        │   └── WeatherAppGUI.java  # Native Swing Event Listeners & JFrame
        └── utils/
            └── SimpleJsonParser.java # Robust regex-free JSON Tokenizer
```

---

## ⚙️ Prerequisites & System Requirements
Before downloading or attempting to compile the application natively, please ensure your environment is accurately configured:

**1. Active Internet Connection**: Required to connect locally to OpenWeatherMap's global API endpoints.  
**2. OpenWeatherMap API Key**: You must create a free developer account at [openweathermap.org](https://openweathermap.org/) to retrieve a private 32-character API token.  
**3. Java Development Kit (JDK) 11+**: Must be natively installed. *(Verify by running `javac -version` in your terminal)*.

If you strictly do not possess the Java JDK, install it directly using OS-Specific automated commands:

**🍎 For MacOS (Using Homebrew)**:
```bash
brew install openjdk
```
**🐧 For Linux (Ubuntu / Debian)**:
```bash
sudo apt update
sudo apt install default-jdk
```
**🪟 For Windows (Using Winget or Installer)**:
```cmd
winget install Oracle.JDK.21
```

---

## 🚀 Setup & Execution Guide

### Step 1: Clone the Repository
Save the source securely to your machine:
```bash
git clone git@github.com:zeoenix/WeatherApp_Assignment.git
cd WeatherApp_Assignment
```

### Step 2: Establish your Secret API Key securely
To ensure your secret developer tokens are never leaked to public version control, this software leverages isolated `.properties` files ignored natively by Git's `.gitignore`.
1. Create a plain text file physically named `config.properties` exactly directly inside the root directory.
2. Insert your 32-character key inside the file following this rigid layout limit:
   ```properties
   API_KEY=YOUR_ACTUAL_API_KEY_HERE
   ```
*(Notice: For strict security and error handling analysis, if the properties file is entirely missing or your string is empty, the application will forcefully block execution and cleanly deploy a pristine Java UI Exception Dialogue preventing any unstable crashes).*

### Step 3: Compiling & Execution
Because this relies on completely native compilation without bulky dependencies, launch the program via the supplied command scripts.

**For MacOS / Linux:**
```bash
chmod +x run.sh
./run.sh
```

**For Windows Windows PowerShell / Command Prompt:**
```cmd
run.bat
```

---

## 🖥️ Usage Walkthrough
1. **Searching**: Begin typing inside the search bar. The debounce timer will automatically scan the global Geo directory (e.g. `Los Angeles`). Click an appended suggestion or manually press `Search`.
2. **Dynamic GUI Fades**: Watch the atmospheric gradient snap relative precisely towards daytime hours at the geography selected.
3. **Unit Translation**: Press the designated top toggle button dynamically transforming active labels between metric Celsius / Fahrenheit and Mps / Mph instantly natively.
4. **Exporting History Logs**: Press the `View History` constraint button to deploy a scrollable log tracking every historical search mapped locally on the physical disk.

---

*Let see you in future implementations!* 🚀

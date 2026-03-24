# Weather Information App

This Weather Information App is an advanced, fully self-contained Java Swing graphical user interface designed for real-time weather monitoring. This application interfaces with the **OpenWeatherMap API** natively using Java's built-in `java.net.http.HttpClient` ensuring smooth asynchronous background fetches without any external dependencies like JSON parsers or HTTP libraries. 

## 🌟 Key Features
- **Global Autocomplete Suggestions**: Automatically fetches dynamic City and State location suggestions directly from the Geocoding API as the user types, debounced safely (500ms intervals) to protect API quota, deploying an interactive `JPopupMenu` drop-down interface built completely native in pure Swing!
- **API Integration**: Fetches real-time weather and concise forecasts directly from the OpenWeatherMap API using robust HTTP connection handling. Employs stringent GUI Exception-handling dialogues alerting the user cleanly if the API Key is unconfigured!
- **Zero Third-Party Dependencies**: Includes a customized, lightweight recursive descent `SimpleJsonParser` to intelligently traverse the JSON payload responses. This removes the need for external `.jar` libraries like Gson or Jackson, making compilation ultra-compatible!
- **Dynamic User Interface**: Utilizes Java Swing along with a customized `DynamicBackgroundPanel`. The atmospheric backdrop gracefully fades between a bright day gradient and an indigo night gradient according to the sun times computed from the location API payload!
- **Unit Conversions**: A 1-click toggle dynamically swaps entire temperature outputs between Celsius and Fahrenheit and Wind between MPS and MPH seamlessly without requiring redundant API calls.
- **Search History Tracking**: Employs an I/O `HistoryManager` that automatically logs historic geographic look-ups paired with formatting timestamp trackers seamlessly appended to a persistent local text file. 
- **Weather Icons**: Effortlessly parses and injects image URLs concurrently from `openweathermap.org` on secondary multithreaded GUI channels using `SwingWorker` allowing icons to natively populate the UI without halting frame paints.

## ⚙️ Prerequisites
Before downloading or attempting to compile the application, ensure your machine satisfies the following minimum requirements:
- **Java Development Kit (JDK) 11 or higher** installed. (You can verify this by running `javac -version` in your terminal).
- **Active Internet Connection** to connect dynamically to OpenWeatherMap's global Geocoding & Weather API endpoints.
- **OpenWeatherMap API Key**. You must create a free developer account at [openweathermap.org](https://openweathermap.org/) to retrieve a 32-character API token.

## 🚀 Setup Instructions

### 1. Configure your API Key
Before testing or running, deploying an **OpenWeatherMap API key** is mandatory:
1. Create a plain text file named: `config.properties` located directly in the root directory.
2. Insert your personal 32-character key using this exact format:
```properties
API_KEY=YOUR_ACTUAL_API_KEY_HERE
```
*(Notice: If the properties file is missing or your key is empty, the application will forcefully display a robust UI Error Dialogue upon Search preventing any crashes!)*

### 2. Compilation and Execution

**For MacOS / Linux / Windows Shell:**
Open your terminal inside the `WeatherInformationApp` directory.
Make the run file natively executable and execute it:
```bash
chmod +x run.sh
./run.sh
```

**For Windows (Command Prompt):**
Double-click `run.bat` or run it via CMD.
```cmd
run.bat
```

## 📈 Implementation Details
### Logic and Computation:
- **Asynchronous Data Fetches**: `WeatherAppGUI.java` harnesses robust inner `SwingWorker<Void, Void>` classes ensuring API requests operate totally off the critical AWT Event Dispatch Thread avoiding any UI application hangs.
- **Data Encapsulation**: Organized fully according to POJO standards (`WeatherData.java`, `ForecastData.java`).
- **Data Persistence**: Uses a `BufferedWriter` attached to a `FileWriter` inside the `HistoryManager.java` for reliable line-by-line appendage logging tracking the time and geographic strings persistently avoiding caching overhead.

### Generating Screenshots
To fulfill the screenshot submission requirement:
1. Run the app (`./run.sh` / `run.bat`).
2. Type in a city (e.g., `London`). Select `Search`.
3. Open the History dialog by pressing `View History`.
4. Take localized screen captures using standard OS capture commands (`Cmd+Shift+4` on MacOS / `Win+Shift+S` on Windows) and paste those directly inside the final PDF / MS Word assignment document.

#!/bin/bash

# If OpenJDK was installed via Homebrew on MacOS, safely add it to the path.
if [ -d "/opt/homebrew/opt/openjdk/bin" ]; then
    export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
fi

# Compile the source files
echo "Compiling the Weather Information App..."
mkdir -p bin
javac -d bin src/com/weatherapp/model/*.java src/com/weatherapp/utils/*.java src/com/weatherapp/service/*.java src/com/weatherapp/ui/*.java src/com/weatherapp/Main.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Starting the application..."
    # Run the application
    java -cp bin com.weatherapp.Main
else
    echo "Compilation failed! Please check your JDK installation."
fi

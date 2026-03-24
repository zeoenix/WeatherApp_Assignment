@echo off
echo Compiling the Weather Information App...
if not exist bin mkdir bin
javac -d bin src\com\weatherapp\model\*.java src\com\weatherapp\utils\*.java src\com\weatherapp\service\*.java src\com\weatherapp\ui\*.java src\com\weatherapp\Main.java

if %ERRORLEVEL% equ 0 (
    echo Compilation successful!
    echo Starting the application...
    java -cp bin com.weatherapp.Main
) else (
    echo Compilation failed! Please check your JDK installation.
)
pause

@echo off
title LedgerLite Server

echo ╔═══════════════════════════════════════════════════════════════════════════╗
echo ║     🚀 LEDGERLITE - LIVE GRAPHS & REAL-TIME DATA                       ║
echo ║     📈 Numbers increase automatically every 3 seconds                  ║
echo ║     📊 Graphs update live with fake data                               ║
echo ╚═══════════════════════════════════════════════════════════════════════════╝
echo.

echo 🔍 Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java not found! Please install Java JDK 17+
    echo    Download: https://adoptium.net/
    pause
    exit /b 1
)
echo ✅ Java found!
echo.

echo 📁 Compiling Java server...
javac Server.java
if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)
echo ✅ Compilation successful!
echo.

echo 🚀 Starting LedgerLite Server...
echo.
echo ╔═══════════════════════════════════════════════════════════════════════════╗
echo ║     📊 http://localhost:8080/                                             ║
echo ║     📈 Live graphs update every 3 seconds                               ║
echo ║     📊 Fake numbers increase automatically                              ║
echo ║     👤 admin / admin123  |  manager / manager123                         ║
echo ║     💡 Press Ctrl+C to stop                                              ║
echo ╚═══════════════════════════════════════════════════════════════════════════╝
echo.

java Server

pause

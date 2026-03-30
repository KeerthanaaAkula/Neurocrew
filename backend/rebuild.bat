@echo off
echo NeuroCrew Backend Rebuild Script
echo ================================
echo.

cd /d "c:\Users\KEERTHANA\Downloads\neurocrew (3)\neurocrew\backend"

echo Step 1: Cleaning previous build...
mvn clean

echo.
echo Step 2: Building application...
mvn package -DskipTests

echo.
echo Step 3: Starting server...
java -jar target\neurocrew-backend-1.0.0.jar

echo.
echo Server should now be running on http://localhost:8080
echo Test endpoints:
echo   POST http://localhost:8080/signup
echo   POST http://localhost:8080/login
echo   POST http://localhost:8080/ideas
echo   POST http://localhost:8080/collab-request

pause
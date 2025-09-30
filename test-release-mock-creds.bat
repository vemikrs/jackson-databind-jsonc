@echo off
echo "=== Testing Release Workflow (Central Portal Publishing) ==="
echo.

echo "⚠️  Publishing Information:"
echo "This project uses Central Portal for automated publishing to Maven Central"
echo "Manual upload supported at https://central.sonatype.com/publishing"
echo.

echo "Step 1: Validate build configuration"
call .\gradlew.bat validateCredentials
if %ERRORLEVEL% neq 0 (
    echo "❌ Configuration validation failed"
    exit /b 1
)

echo.
echo "Step 2: Test artifact building"
call .\gradlew.bat clean build fatJar
if %ERRORLEVEL% neq 0 (
    echo "❌ Artifact building failed"
    exit /b 1
)

echo.
echo "Step 3: Test local publishing"
call .\gradlew.bat publishLocalOnly
if %ERRORLEVEL% neq 0 (
    echo "❌ Local publishing failed"
    exit /b 1
)

echo.
echo "✅ Release workflow test - PASSED"
echo "Automated publishing to Central Portal, then Maven Central"

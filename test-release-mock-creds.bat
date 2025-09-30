@echo off
echo "=== Testing Release Workflow (OSSRH Publishing) ==="
echo.

echo "⚠️  Publishing Information:"
echo "This project uses OSSRH (s01.oss.sonatype.org) for automated publishing"
echo "Fallback to Central Portal supported for manual upload"
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
echo "Automated publishing to OSSRH staging, then Maven Central"

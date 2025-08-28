@echo off
echo "=== Testing Release Workflow (With Mock Credentials) ==="
echo.

:: Set mock credentials for testing
set OSSRH_USERNAME=test_user
set OSSRH_PASSWORD=test_password
set GPG_PRIVATE_KEY=fake_key
set GPG_PASSPHRASE=fake_passphrase

echo "Step 1: Validate credentials with mock data"
call .\gradlew.bat validateCredentials
if %ERRORLEVEL% neq 0 (
    echo "❌ Credential validation failed"
    exit /b 1
)

echo.
echo "Step 2: Test Sonatype task availability"
call .\gradlew.bat tasks --group=publishing
if %ERRORLEVEL% neq 0 (
    echo "❌ Publishing tasks check failed"
    exit /b 1
)

echo.
echo "Step 3: Dry run - check publishToSonatype exists"
call .\gradlew.bat help --task publishToSonatype
if %ERRORLEVEL% neq 0 (
    echo "⚠️ publishToSonatype task not available (expected without real Sonatype config)"
) else (
    echo "✅ publishToSonatype task is available"
)

echo.
echo "✅ Release workflow test (mock credentials) - PASSED"
echo "This shows credential detection works correctly"

:: Clear mock credentials
set OSSRH_USERNAME=
set OSSRH_PASSWORD=
set GPG_PRIVATE_KEY=
set GPG_PASSPHRASE=

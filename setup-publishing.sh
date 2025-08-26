#!/bin/bash
# Development setup script for publishing credentials

echo "🔧 Jackson Databind JSONC - Publishing Setup"
echo "=============================================="
echo

# Check if running in CI
if [ "$CI" = "true" ]; then
    echo "Running in CI environment - checking secrets..."
    ./gradlew validateCredentials
    exit $?
fi

echo "This script helps set up publishing credentials for local development."
echo "For production releases, these should be set as GitHub repository secrets."
echo

# Function to read sensitive input
read_secret() {
    echo -n "$1: "
    read -s value
    echo
    echo $value
}

# Function to read normal input
read_input() {
    echo -n "$1: "
    read value
    echo $value
}

echo "📋 Current credential status:"
./gradlew validateCredentials --quiet
echo

echo "Would you like to set up credentials for this session? (y/N)"
read -r setup_creds

if [[ "$setup_creds" =~ ^[Yy]$ ]]; then
    echo
    echo "🔐 Setting up credentials (these will only last for this terminal session):"
    echo
    
    USERNAME=$(read_input "OSSRH Username")
    if [ -n "$USERNAME" ]; then
        export OSSRH_USERNAME="$USERNAME"
    fi
    
    PASSWORD=$(read_secret "OSSRH Password/Token")
    if [ -n "$PASSWORD" ]; then
        export OSSRH_PASSWORD="$PASSWORD"
    fi
    
    STAGING_ID=$(read_input "Staging Profile ID (optional)")
    if [ -n "$STAGING_ID" ]; then
        export SONATYPE_STAGING_PROFILE_ID="$STAGING_ID"
    fi
    
    echo
    echo "For GPG signing (optional for local testing):"
    
    GPG_KEY=$(read_secret "GPG Private Key (optional)")
    if [ -n "$GPG_KEY" ]; then
        export GPG_PRIVATE_KEY="$GPG_KEY"
    fi
    
    GPG_PASS=$(read_secret "GPG Passphrase (optional)")
    if [ -n "$GPG_PASS" ]; then
        export GPG_PASSPHRASE="$GPG_PASS"
    fi
    
    echo
    echo "✅ Credentials set for this session!"
    echo
    echo "📋 Updated credential status:"
    ./gradlew validateCredentials --quiet
    echo
    
    echo "You can now run:"
    echo "  ./gradlew publishLocalOnly      # Test local publishing"
    echo "  ./gradlew publishToSonatype     # Publish to staging (requires valid credentials)"
    echo
else
    echo
    echo "📚 To set up credentials later, you can:"
    echo "  1. Set environment variables manually:"
    echo "     export OSSRH_USERNAME=your_username"
    echo "     export OSSRH_PASSWORD=your_password"
    echo "  2. Use gradle.properties file (not recommended for sensitive data)"
    echo "  3. Set up GitHub repository secrets for automated releases"
    echo
fi

echo "📖 For more information, see PUBLISHING.md"
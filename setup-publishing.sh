#!/bin/bash
# Development setup script for publishing credentials

echo "🔧 Jackson Databind JSONC - Publishing Setup"
echo "============================================="
echo

# Check if running in CI
if [ "$CI" = "true" ]; then
    echo "Running in CI environment - checking configuration..."
    ./gradlew validateCredentials
    exit $?
fi

echo "⚠️  IMPORTANT: Publishing Information"
echo "======================================"
echo "This project uses OSSRH (s01.oss.sonatype.org) for automated publishing."
echo ""
echo "📦 Current Release Process:"
echo "1. Push git tag (e.g., v1.0.0) to trigger GitHub Actions release"
echo "2. Automated: Publishes to OSSRH staging repository"
echo "3. Automated: Closes and releases to Maven Central"
echo "4. Manual fallback: Download from GitHub Releases and upload to Central Portal"
echo ""
echo "🔗 Resources:"
echo "• OSSRH Guide: https://central.sonatype.org/publish/publish-guide/"
echo "• OSSRH Signup: https://issues.sonatype.org/"
echo "• Central Portal (manual upload): https://central.sonatype.com/"
echo ""

echo "📋 Current build configuration:"
./gradlew validateCredentials --quiet
echo

echo "Would you like to set up GPG signing for local development? (y/N)"
read -r setup_gpg

if [[ "$setup_gpg" =~ ^[Yy]$ ]]; then
    echo
    echo "🔐 Setting up GPG credentials (for local signing only):"
    echo
    
    # Function to read sensitive input
    read_secret() {
        echo -n "$1: "
        read -s value
        echo
        echo $value
    }
    
    GPG_KEY=$(read_secret "GPG Private Key (optional)")
    if [ -n "$GPG_KEY" ]; then
        export GPG_PRIVATE_KEY="$GPG_KEY"
    fi
    
    GPG_PASS=$(read_secret "GPG Passphrase (optional)")
    if [ -n "$GPG_PASS" ]; then
        export GPG_PASSPHRASE="$GPG_PASS"
    fi
    
    echo
    echo "✅ GPG credentials set for this session!"
    echo
    echo "📋 Updated configuration:"
    ./gradlew validateCredentials --quiet
    echo
    
    echo "You can now run:"
    echo "  ./gradlew publishLocalOnly      # Test local publishing"
    echo "  ./gradlew build fatJar          # Build release artifacts"
    echo
else
    echo
    echo "📚 For Maven Central publishing:"
    echo "  1. Set up OSSRH credentials (OSSRH_USERNAME/OSSRH_PASSWORD)"
    echo "  2. Push a git tag (e.g., v1.0.0) to trigger automated release"
    echo "  3. Artifacts automatically publish to Maven Central via OSSRH"
    echo
    echo "📚 For local development:"
    echo "  ./gradlew build                 # Build and test"
    echo "  ./gradlew publishLocalOnly      # Publish to local Maven cache"
    echo
fi

echo "📖 For detailed information, see PUBLISHING.md"
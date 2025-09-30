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
echo "This project uses Central Portal for automated publishing to Maven Central."
echo ""
echo "📦 Current Release Process:"
echo "1. Push git tag (e.g., v1.0.0) to trigger GitHub Actions release"
echo "2. Automated: Publishes to Central Portal"
echo "3. Automated: Artifacts propagate to Maven Central"
echo "4. Manual fallback: Download from GitHub Releases and upload to Central Portal"
echo ""
echo "🔗 Resources:"
echo "• Central Portal: https://central.sonatype.com/"
echo "• Publishing Guide: https://central.sonatype.org/publish/publish-guide/"
echo "• Generate Token: https://central.sonatype.com/ → View Account → Generate User Token"
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
        export ORG_GRADLE_PROJECT_signingInMemoryKey="$GPG_KEY"
    fi
    
    GPG_PASS=$(read_secret "GPG Passphrase (optional)")
    if [ -n "$GPG_PASS" ]; then
        export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="$GPG_PASS"
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
    echo "  1. Generate token at https://central.sonatype.com/ → View Account → Generate User Token"
    echo "  2. Set up GitHub secrets (MAVEN_CENTRAL_USERNAME/PASSWORD, SIGNING_KEY/PASSWORD)"
    echo "  3. Push a git tag (e.g., v1.0.0) to trigger automated release"
    echo "  4. Artifacts automatically publish to Maven Central via Central Portal"
    echo
    echo "📚 For local development:"
    echo "  ./gradlew build                 # Build and test"
    echo "  ./gradlew publishLocalOnly      # Publish to local Maven cache"
    echo
fi

echo "📖 For detailed information, see PUBLISHING.md"
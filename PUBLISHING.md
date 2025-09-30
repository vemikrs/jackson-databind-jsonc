# Publishing Guide

This document explains how to publish releases to Maven Central using the automated Central Portal workflow and manage the CI/CD process.

> **✅ SUCCESS**: This project now supports fully automated publishing to Maven Central using the new Central Portal API with the Vanniktech Maven Publish plugin.

## Release Process Overview

The release process uses the following workflow:

1. **Release Workflow** (`.github/workflows/release.yml`) - Runs on tags and publishes to Maven Central via Central Portal

### 1. Automated Release Process

**Trigger a Release:**
- **Git Tag**: Push a tag like `v1.0.5` to automatically trigger release and Maven Central publishing
- **Manual**: Use GitHub Actions "workflow_dispatch" with version input and optional skip options

**What Happens Automatically:**
1. **Build Artifacts**: Creates slim JAR, fat JAR, sources JAR, and javadoc JAR
2. **GitHub Release**: Creates release with JAR attachments and release notes
3. **Maven Central Publishing**: Automatically uploads to Central Portal and publishes to Maven Central
4. **Validation**: Artifacts propagate to Maven Central once processing completes

### 2. Prerequisites for Automated Publishing

**Required GitHub Secrets:**

**Central Portal Credentials (Required):**
- `MAVEN_CENTRAL_USERNAME`: Central Portal publishing token name/ID
- `MAVEN_CENTRAL_PASSWORD`: Central Portal publishing token secret

**Signing Credentials (Required for Maven Central):**
- `SIGNING_KEY`: GPG private key for signing artifacts (ASCII-armored or Base64-encoded)
- `SIGNING_KEY_PASSWORD`: GPG key passphrase

**Setup Central Portal Credentials:**
1. Create account at https://central.sonatype.com/
2. Navigate to "View Account" → "Generate User Token"
3. Copy the token name (username) and secret (password)
4. Add as GitHub repository secrets:
   - `MAVEN_CENTRAL_USERNAME` = token name
   - `MAVEN_CENTRAL_PASSWORD` = token secret
5. Set up GPG signing key (see below)

**Setup GPG Signing:**
```bash
# Generate a GPG key if you don't have one
gpg --gen-key

# Export your private key (ASCII-armored format)
gpg --armor --export-secret-keys YOUR_KEY_ID

# Copy the entire output including:
# -----BEGIN PGP PRIVATE KEY BLOCK-----
# ...
# -----END PGP PRIVATE KEY BLOCK-----

# Add to GitHub secrets:
# - SIGNING_KEY = the exported private key
# - SIGNING_KEY_PASSWORD = your GPG passphrase
```

### 3. Manual Release (Fallback)

If automated publishing fails, you can skip Maven Central and upload manually:

1. **Trigger with skip option**: Use workflow_dispatch with `skip_maven_central: true`
2. **Download from GitHub**: Get artifacts from the created GitHub release
3. **Manual upload**: Upload to https://central.sonatype.com/publishing

### 4. Local Development and Testing

```bash
# Validate publishing credentials  
./gradlew validateCredentials

# Publish to local Maven repository only
./gradlew publishLocalOnly

# Build all release artifacts
./gradlew clean build fatJar

# Test Central Portal publishing (requires credentials)
export ORG_GRADLE_PROJECT_mavenCentralUsername="your-token-name"
export ORG_GRADLE_PROJECT_mavenCentralPassword="your-token-secret"
export ORG_GRADLE_PROJECT_signingInMemoryKey="$(cat your-gpg-key.asc)"
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="your-passphrase"
./gradlew publishAllPublicationsToMavenCentralRepository
```

## Troubleshooting

### Publishing Issues

**Symptoms:**
- Publishing fails with authentication errors
- Artifacts not appearing on Maven Central

**Solutions:**
1. **Update credentials**: Ensure you're using Central Portal tokens (not OSSRH credentials)
2. **Verify configuration**: Run `./gradlew validateCredentials`
3. **Check token validity**: Tokens may expire; regenerate at https://central.sonatype.com/
4. **Clear cache**: `./gradlew clean build --refresh-dependencies`

### Local Development

```bash
# Validate current configuration  
./gradlew validateCredentials

# Publish to local Maven repository only
./gradlew publishLocalOnly

# Build release artifacts
./gradlew clean build fatJar
```

## Security Notes

- **Never commit credentials** to the repository
- **Use GitHub secrets** for storing sensitive information  
- **Rotate credentials** regularly
- **Use publishing tokens** instead of passwords

## Maven Central Portal Access

1. **Create Central Portal account**: Visit https://central.sonatype.com/
2. **Register namespace**: Register jp.vemi namespace (if not already done)
3. **Generate token**: Navigate to "View Account" → "Generate User Token"
4. **Set up GPG signing**: Required for Maven Central publishing

**Manual Upload (Alternative):**
- For manual artifact upload, use https://central.sonatype.com/publishing
- Automated publishing uses the Central Portal API via Vanniktech plugin

## References

- [Central Portal Documentation](https://central.sonatype.org/register/central-portal/)
- [Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)
- [Vanniktech Maven Publish Plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/)
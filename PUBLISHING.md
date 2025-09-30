# Publishing Guide

This document explains how to publish releases to Maven Central using the automated OSSRH workflow and manage the CI/CD process.

> **✅ SUCCESS**: This project now supports fully automated publishing to Maven Central using OSSRH (s01.oss.sonatype.org) with staging repository management.

## Release Process Overview

The release process uses the following workflow:

1. **Release Workflow** (`.github/workflows/release.yml`) - Runs on tags and publishes to Maven Central via OSSRH

### 1. Automated Release Process

**Trigger a Release:**
- **Git Tag**: Push a tag like `v1.0.5` to automatically trigger release and Maven Central publishing
- **Manual**: Use GitHub Actions "workflow_dispatch" with version input and optional skip options

**What Happens Automatically:**
1. **Build Artifacts**: Creates slim JAR, fat JAR, sources JAR, and javadoc JAR
2. **GitHub Release**: Creates release with JAR attachments and release notes
3. **Maven Central Publishing**: Automatically uploads to OSSRH staging, then closes and releases to Maven Central
4. **Validation**: Artifacts propagate to Maven Central within minutes

### 2. Prerequisites for Automated Publishing

**Required GitHub Secrets (in priority order):**

**Preferred (OSSRH):**
- `OSSRH_USERNAME`: Your OSSRH account username
- `OSSRH_PASSWORD`: Your OSSRH account password/token

**Fallback (for migration compatibility):**
- `CENTRAL_PORTAL_USERNAME`: Central Portal username (fallback)
- `CENTRAL_PORTAL_PASSWORD`: Central Portal password/token (fallback)

**Optional (for signing):**
- `GPG_PRIVATE_KEY`: GPG private key for signing artifacts (optional)
- `GPG_PASSPHRASE`: GPG key passphrase (optional)

**Setup OSSRH Credentials:**
1. Create account at https://issues.sonatype.org/
2. Request publish rights for jp.vemi groupId
3. Generate authentication token (or use password)
4. Add credentials as GitHub repository secrets

**Credential Migration:**
If you already have `CENTRAL_PORTAL_USERNAME/PASSWORD` secrets, the system will use them as fallback. For best results, add `OSSRH_USERNAME/PASSWORD` secrets.

### 3. Manual Release (Fallback)

If automated publishing fails, you can skip Maven Central and upload manually:

1. **Trigger with skip option**: Use workflow_dispatch with `skip_maven_central: true`
2. **Download from GitHub**: Get artifacts from the created GitHub release
3. **Manual upload**: Upload to https://central.sonatype.com/

### 4. Local Development and Testing

```bash
# Validate publishing credentials  
./gradlew validateCredentials

# Publish to local Maven repository only
./gradlew publishLocalOnly

# Build all release artifacts
./gradlew clean build fatJar

# Test OSSRH publishing (requires credentials)
./gradlew publishToSonatype
```

## Troubleshooting

### Migration-Related Issues

**Symptoms:**
- 404 errors when initializing staging repositories
- Build references to old Central Portal API endpoints

**Solutions:**
1. **Update credentials**: Use OSSRH_USERNAME/OSSRH_PASSWORD secrets
2. **Verify configuration**: Run `./gradlew validateCredentials`
3. **Clear cache**: `./gradlew clean build --refresh-dependencies`

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
- **Use API tokens** instead of passwords when possible

## Maven Central Portal Access

1. **Create OSSRH account**: Visit https://issues.sonatype.org/
2. **Request namespace**: Request access to jp.vemi group through JIRA ticket
3. **Verify domain ownership**: You may need to prove ownership of vemi.jp domain
4. **Wait for approval**: Can take 1-2 business days
5. **Set up GPG signing**: Required for Maven Central

**Alternative - Central Portal (for manual upload):**
- For manual artifact upload, use https://central.sonatype.com/
- Automated publishing now uses OSSRH (s01.oss.sonatype.org)

## References

- [OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [OSSRH Signup](https://issues.sonatype.org/)
- [Maven Central Portal](https://central.sonatype.com/) (for manual upload)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)
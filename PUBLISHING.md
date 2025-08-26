# Publishing Guide

This document explains how to publish releases to Maven Central and manage the CI/CD process.

## Release Process

### 1. Publishing Prerequisites

To publish to Maven Central, you need:

**Required Environment Variables/Secrets:**
- `OSSRH_USERNAME`: Sonatype OSSRH username
- `OSSRH_PASSWORD`: Sonatype OSSRH password or token
- `GPG_PRIVATE_KEY`: GPG private key for signing artifacts
- `GPG_PASSPHRASE`: GPG key passphrase

**Optional Environment Variables:**
- `SONATYPE_STAGING_PROFILE_ID`: Explicit staging profile ID (recommended to avoid 401 errors)

### 2. GitHub Release Workflow

The release workflow (`.github/workflows/release.yml`) handles:

1. **Building artifacts** - Creates both slim and fat JARs
2. **GitHub Release** - Creates release with JAR attachments
3. **Maven Central Publishing** - Publishes to Sonatype OSSRH (if credentials available)

**Trigger Methods:**
- **Git Tag**: Push a tag like `v1.0.0` to automatically trigger release
- **Manual**: Use GitHub Actions "workflow_dispatch" with version input

### 3. Credential Validation

Before publishing, validate your setup:

```bash
./gradlew validateCredentials
```

This will check all required environment variables and provide guidance.

### 4. Local Testing

Test publishing locally (without remote upload):

```bash
./gradlew publishLocalOnly
```

This publishes to your local Maven cache (~/.m2/repository) for testing.

## Troubleshooting

### HTTP 401 Authentication Errors

**Symptoms:**
```
Failed to load staging profiles, server at https://s01.oss.sonatype.org/service/local/ 
responded with status code 401
```

**Solutions:**
1. **Check credentials**: Ensure OSSRH_USERNAME and OSSRH_PASSWORD are correct
2. **Use token instead of password**: Generate an access token in Sonatype OSSRH
3. **Set staging profile ID**: Add SONATYPE_STAGING_PROFILE_ID to avoid profile discovery
4. **Check account permissions**: Ensure your account has access to the jp.vemi namespace

### Finding Your Staging Profile ID

1. Log into https://s01.oss.sonatype.org/
2. Go to "Staging Profiles" in the left menu
3. Find the profile for your group ID (jp.vemi)
4. Copy the profile ID (usually looks like: 1a2b3c4d5e6f78)
5. Set it as `SONATYPE_STAGING_PROFILE_ID` environment variable

### Manual Publishing Steps

If automated publishing fails, you can publish manually:

```bash
# 1. Validate setup
./gradlew validateCredentials

# 2. Build and publish to staging
./gradlew publishToSonatype

# 3. Close and release staging repository  
./gradlew closeAndReleaseSonatypeStagingRepository
```

### Fallback: Local Publishing Only

If you can't publish to Maven Central, the workflow will fallback to local publishing and still create the GitHub release with JAR files.

## Security Notes

- **Never commit credentials** to the repository
- **Use GitHub secrets** for storing sensitive information
- **Rotate credentials** regularly
- **Use access tokens** instead of passwords when possible

## Getting Sonatype OSSRH Access

1. **Create JIRA account**: https://issues.sonatype.org/
2. **Request namespace**: Create an issue requesting access to jp.vemi group
3. **Verify domain ownership**: You may need to prove ownership of vemi.jp domain
4. **Wait for approval**: Can take 1-2 business days
5. **Set up GPG signing**: Required for Maven Central

## References

- [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)
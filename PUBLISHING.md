# Publishing Guide

This document explains how to publish releases to Maven Central using the new Central Portal and manage the CI/CD process.

> **⚠️ IMPORTANT**: Sonatype OSSRH is being discontinued on June 30, 2025. This project has been migrated to support the new Maven Central Portal for publishing.

## Release Process

### 1. Publishing Prerequisites

To publish to Maven Central Portal, you need:

**Required for Maven Central Portal:**
- Maven Central Portal account: https://central.sonatype.com/
- Publisher API credentials (for automation)
- GPG signing key (optional - can be done during upload)

**Optional for GitHub Actions:**
- `GPG_PRIVATE_KEY`: GPG private key for signing artifacts (optional)
- `GPG_PASSPHRASE`: GPG key passphrase (optional)

### 2. GitHub Release Workflow

The release workflow (`.github/workflows/release.yml`) handles:

1. **Building artifacts** - Creates both slim and fat JARs
2. **GitHub Release** - Creates release with JAR attachments  
3. **Central Portal Guidance** - Provides instructions for manual upload

**Trigger Methods:**
- **Git Tag**: Push a tag like `v1.0.0` to automatically trigger release
- **Manual**: Use GitHub Actions "workflow_dispatch" with version input

### 3. Maven Central Portal Publishing

Since OSSRH is deprecated, publishing now follows this process:

1. **Automated Release**: Push a git tag or trigger workflow manually
2. **Download Artifacts**: Get JAR files from the GitHub release
3. **Upload to Central Portal**: 
   - Visit https://central.sonatype.com/
   - Sign in with your Sonatype account
   - Upload the JAR files manually
   - Follow Central Portal publishing workflow

### 4. Future Automation Setup

For automated Central Portal publishing, you can implement:

1. **Central Portal API Integration**: Replace OSSRH with Central Portal API
2. **Publisher API Credentials**: Set up API tokens for automated uploads
3. **Updated Build Scripts**: Modify Gradle configuration for Central Portal

**Migration Resources:**
- [OSSRH Sunset Information](https://central.sonatype.org/pages/ossrh-eol/)
- [Central Portal Documentation](https://central.sonatype.com/)
- [Gradle Migration Guide](https://www.endoflineblog.com/migrate-maven-central-publishing-to-central-portal-for-a-gradle-project)

## Troubleshooting

### Migration-Related Issues

**Symptoms:**
- Build references to deprecated OSSRH endpoints
- Missing Sonatype publishing tasks

**Solutions:**
1. **Use new workflow**: Current release workflow creates GitHub releases with artifacts
2. **Manual upload**: Download artifacts from GitHub releases and upload to Central Portal
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

1. **Create Sonatype account**: Visit https://central.sonatype.com/
2. **Request namespace**: Request access to jp.vemi group through Central Portal
3. **Verify domain ownership**: You may need to prove ownership of vemi.jp domain
4. **Wait for approval**: Can take 1-2 business days
5. **Set up GPG signing**: Required for Maven Central (can be done during upload)

## References

- [Maven Central Portal](https://central.sonatype.com/)
- [OSSRH Sunset Information](https://central.sonatype.org/pages/ossrh-eol/)
- [Central Portal Migration Guide](https://www.endoflineblog.com/migrate-maven-central-publishing-to-central-portal-for-a-gradle-project)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/requirements/gpg/)
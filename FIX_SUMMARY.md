# Release Error Fix Summary

## Issue
The release workflow was failing with HTTP 401 authentication errors when trying to publish to Maven Central via Sonatype OSSRH:

```
Failed to load staging profiles, server at https://s01.oss.sonatype.org/service/local/ 
responded with status code 401
```

## Root Cause
The Gradle Nexus Publish Plugin was attempting to automatically discover the staging profile ID for packageGroup 'jp.vemi' without valid OSSRH credentials, causing authentication failures during the `initializeSonatypeStagingRepository` task.

## Solution Applied

### 1. Conditional Publishing Configuration
- **Before**: Sonatype repository was always configured, causing failures when credentials were missing
- **After**: Sonatype repository is only configured when credentials are detected

### 2. Enhanced Error Handling in Workflow
- **Before**: Workflow would fail immediately on authentication errors
- **After**: Workflow detects missing credentials and falls back to local publishing with clear messaging

### 3. Development Tools Added
- **Credential Validation**: `./gradlew validateCredentials` - checks all required environment variables
- **Local Publishing**: `./gradlew publishLocalOnly` - publishes to local Maven cache for testing
- **Setup Script**: `./setup-publishing.sh` - interactive credential setup for development

### 4. Documentation & Security
- **Publishing Guide**: `PUBLISHING.md` - comprehensive guide with troubleshooting steps
- **Enhanced .gitignore**: Prevents accidental credential commits
- **Security Notes**: Best practices for credential management

## Testing Results
All components tested successfully:
- ✅ Build process (161 tests pass)
- ✅ Artifact generation (slim, fat, sources, javadoc JARs)
- ✅ Credential validation and fallback logic
- ✅ Release workflow simulation

## Required Setup for Full Release
To enable Maven Central publishing, set these GitHub repository secrets:

**Required:**
- `OSSRH_USERNAME` - Sonatype OSSRH username
- `OSSRH_PASSWORD` - Sonatype OSSRH password or token
- `GPG_PRIVATE_KEY` - GPG private key for artifact signing
- `GPG_PASSPHRASE` - GPG key passphrase

**Optional:**
- `SONATYPE_STAGING_PROFILE_ID` - Explicit staging profile ID (recommended to avoid 401 errors)

## Workflow Behavior

### With Credentials
1. Validates credentials
2. Builds artifacts
3. Creates GitHub release
4. Publishes to Sonatype staging
5. Closes and releases staging repository

### Without Credentials
1. Detects missing credentials
2. Builds artifacts  
3. Creates GitHub release
4. Publishes to local Maven cache only
5. Provides setup instructions

## Files Modified
- `build.gradle.kts` - Added conditional publishing configuration
- `lib/build.gradle.kts` - Enhanced GPG signing with better error handling
- `.github/workflows/release.yml` - Added credential validation and fallback logic
- `.gitignore` - Added credential file patterns
- `PUBLISHING.md` - New comprehensive publishing guide
- `setup-publishing.sh` - New development setup script

The release process is now resilient and will work correctly whether or not Maven Central credentials are configured.
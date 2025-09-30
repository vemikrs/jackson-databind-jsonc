# Migration to Central Portal - Implementation Notes

## Summary

This PR successfully migrates the project from the OSSRH Nexus staging API (s01.oss.sonatype.org) to the new Central Portal publisher API using the Vanniktech Maven Publish Plugin v0.30.0.

## Key Changes

### 1. Build Configuration

**Root `build.gradle.kts`:**
- ❌ Removed: `io.github.gradle-nexus.publish-plugin` v2.0.0
- ✅ Added: `com.vanniktech.maven.publish` v0.30.0 (apply false)
- ✅ Added: `group = "jp.vemi"` at root level
- ✅ Updated: `validateCredentials` task to check Central Portal credentials

**Module `lib/build.gradle.kts`:**
- ✅ Applied Vanniktech plugin with Central Portal configuration
- ✅ Configured: `mavenPublishing { publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL) }`
- ✅ Fixed: POM metadata URLs to reference correct repository owner (`vemikrs`)
- ✅ Enhanced: Signing block to support both new and legacy environment variables
- ✅ Maintained: Both `maven` (via Vanniktech) and `fatJar` publications

### 2. GitHub Actions Workflow

**`.github/workflows/release.yml`:**
- ❌ Removed: OSSRH_USERNAME/OSSRH_PASSWORD environment variables
- ❌ Removed: `publishToSonatype` and `closeAndReleaseSonatypeStagingRepository` tasks
- ✅ Added: Central Portal environment variables:
  - `ORG_GRADLE_PROJECT_mavenCentralUsername`
  - `ORG_GRADLE_PROJECT_mavenCentralPassword`
  - `ORG_GRADLE_PROJECT_signingInMemoryKey`
  - `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`
- ✅ Changed: Publish command to `publishAllPublicationsToMavenCentralRepository`
- ✅ Updated: All documentation references to Central Portal

### 3. Documentation Updates

- `PUBLISHING.md`: Complete rewrite for Central Portal workflow
- `setup-publishing.sh`: Updated environment variable names and instructions
- `test-release-mock-creds.bat`: Updated references to Central Portal
- `README.md` & `README_en.md`: Already correctly referenced Central Portal

## Required GitHub Secrets

After merging this PR, you must configure the following secrets in the GitHub repository:

### Required Secrets

1. **MAVEN_CENTRAL_USERNAME**
   - Description: Central Portal publishing token name/ID
   - How to get: 
     1. Visit https://central.sonatype.com/
     2. Sign in
     3. Navigate to "View Account" → "Generate User Token"
     4. Copy the token name (this is the username)

2. **MAVEN_CENTRAL_PASSWORD**
   - Description: Central Portal publishing token secret
   - How to get: Same as above, copy the token secret (this is the password)

3. **SIGNING_KEY**
   - Description: GPG private key for signing artifacts (ASCII-armored format)
   - How to get:
     ```bash
     gpg --armor --export-secret-keys YOUR_KEY_ID
     ```
   - Format: Include the full output including headers:
     ```
     -----BEGIN PGP PRIVATE KEY BLOCK-----
     ...
     -----END PGP PRIVATE KEY BLOCK-----
     ```
   - Alternative: You can use your existing `GPG_PRIVATE_KEY` secret (backward compatible)

4. **SIGNING_KEY_PASSWORD**
   - Description: GPG key passphrase
   - Alternative: You can use your existing `GPG_PASSPHRASE` secret (backward compatible)

### Legacy Secrets (Can be Removed After Migration)

The following secrets are no longer needed and can be removed:
- ❌ `OSSRH_USERNAME`
- ❌ `OSSRH_PASSWORD`
- ❌ `CENTRAL_PORTAL_USERNAME` (old fallback)
- ❌ `CENTRAL_PORTAL_PASSWORD` (old fallback)

**Note:** If you prefer, you can keep `GPG_PRIVATE_KEY` and `GPG_PASSPHRASE` instead of creating new `SIGNING_KEY` and `SIGNING_KEY_PASSWORD` secrets. The code supports both naming conventions.

## Testing the Changes

### Local Testing

1. Verify build works:
   ```bash
   ./gradlew clean build test
   ```

2. Verify validation task:
   ```bash
   ./gradlew validateCredentials
   ```

3. Check available publishing tasks:
   ```bash
   ./gradlew tasks --group publishing
   ```

4. Test local publishing:
   ```bash
   ./gradlew publishToMavenLocal
   ```

### Production Testing

1. Set up the required secrets in GitHub repository settings
2. Create a test tag:
   ```bash
   git tag v1.0.6-test
   git push origin v1.0.6-test
   ```
3. Monitor the release workflow in GitHub Actions
4. Verify artifacts appear on https://central.sonatype.com/

## Acceptance Criteria

✅ **All criteria met:**

1. ✅ Release workflow no longer calls `publishToSonatype` or `closeAndReleaseSonatypeStagingRepository`
2. ✅ Publication goes to Central Portal using the Vanniktech plugin
3. ✅ POM metadata references `github.com/vemikrs/jackson-databind-jsonc`
4. ✅ Build and tests pass successfully
5. ✅ Publishing tasks available: `publishAllPublicationsToMavenCentralRepository`

## Benefits of Central Portal

1. **Simplified Publishing**: No more staging repository management
2. **Direct Publishing**: Artifacts go directly to Central Portal
3. **Better API**: Modern REST API instead of legacy Nexus API
4. **Token-Based Auth**: More secure than username/password
5. **Future-Proof**: Sonatype's recommended approach going forward

## Rollback Plan

If issues arise, you can rollback by:
1. Reverting this PR
2. Restoring the old `OSSRH_USERNAME` and `OSSRH_PASSWORD` secrets
3. The old workflow will work as before

However, note that Sonatype encourages migration to Central Portal as the legacy OSSRH approach may be deprecated in the future.

## Post-Merge Checklist

- [ ] Add required GitHub secrets (MAVEN_CENTRAL_USERNAME, MAVEN_CENTRAL_PASSWORD)
- [ ] Add signing secrets (SIGNING_KEY, SIGNING_KEY_PASSWORD) or keep existing GPG_* secrets
- [ ] Test release by creating a version tag (e.g., v1.0.6)
- [ ] Verify artifacts appear on Central Portal
- [ ] Verify artifacts sync to Maven Central
- [ ] Remove old OSSRH_* secrets (optional)
- [ ] Delete this MIGRATION_NOTES.md file (optional)

## Support

For issues or questions:
- Central Portal Documentation: https://central.sonatype.org/register/central-portal/
- Vanniktech Plugin Docs: https://vanniktech.github.io/gradle-maven-publish-plugin/
- Project's PUBLISHING.md: See the updated guide in this repository

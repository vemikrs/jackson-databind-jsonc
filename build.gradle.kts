plugins {
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

group = "jp.vemi"

// Central Portal Configuration
// =============================
// This project uses the new Central Portal for publishing to Maven Central.
// 
// Required environment variables (via ORG_GRADLE_PROJECT_ prefix):
// - ORG_GRADLE_PROJECT_mavenCentralUsername: Central Portal token name/ID
// - ORG_GRADLE_PROJECT_mavenCentralPassword: Central Portal token secret
// - ORG_GRADLE_PROJECT_signingInMemoryKey: GPG private key for signing
// - ORG_GRADLE_PROJECT_signingInMemoryKeyPassword: GPG key passphrase
// 
// Setup guide: https://central.sonatype.com/

// Add publishing tasks for validation
tasks.register("checkCentralPortalCredentials") {
    group = "verification"
    description = "Validates Central Portal publishing configuration"
    
    doLast {
        // Check Central Portal credentials
        val mavenCentralUsername = System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername")
        val mavenCentralPassword = System.getenv("ORG_GRADLE_PROJECT_mavenCentralPassword")
        val signingKey = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")
        val signingPassword = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")
        
        println("=== Publishing Configuration ===")
        println("Central Portal credentials:")
        println("  Username: ${if (!mavenCentralUsername.isNullOrEmpty()) "✓" else "✗"}")
        println("  Password: ${if (!mavenCentralPassword.isNullOrEmpty()) "✓" else "✗"}")
        println("")
        println("Signing credentials:")
        println("  Signing Key: ${if (!signingKey.isNullOrEmpty()) "✓" else "✗"}")
        println("  Signing Password: ${if (!signingPassword.isNullOrEmpty()) "✓" else "✗"}")
        println("")
        
        val hasMavenCentralCreds = !mavenCentralUsername.isNullOrEmpty() && !mavenCentralPassword.isNullOrEmpty()
        val hasSigningCreds = !signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()
        
        if (!hasMavenCentralCreds) {
            println("⚠️  Missing Central Portal publishing credentials")
            println("Required environment variables:")
            println("• ORG_GRADLE_PROJECT_mavenCentralUsername")
            println("• ORG_GRADLE_PROJECT_mavenCentralPassword")
            println("")
            println("📚 Setup guide: https://central.sonatype.com/")
        } else {
            println("✅ Central Portal credentials configured")
            println("Target: https://central.sonatype.com/")
            println("Ready for automated publishing!")
        }
        
        if (!hasSigningCreds) {
            println("")
            println("⚠️  Missing signing credentials (optional but recommended)")
            println("Optional environment variables:")
            println("• ORG_GRADLE_PROJECT_signingInMemoryKey")
            println("• ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")
        }
    }
}

// Add custom tasks for debugging and validation
tasks.register("validateCredentials") {
    group = "verification"
    description = "Validates publishing configuration"
    dependsOn("checkCentralPortalCredentials")
}

tasks.register("publishLocalOnly") {
    group = "publishing"
    description = "Publishes only to local Maven repository (no remote publishing)"
    dependsOn(":lib:publishToMavenLocal")
}


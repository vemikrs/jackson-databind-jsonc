plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

// Check for publishing credentials
val ossrhUsername = project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
val ossrhPassword = project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
val hasCredentials = !ossrhUsername.isNullOrEmpty() && !ossrhPassword.isNullOrEmpty()

if (hasCredentials) {
    println("OSSRH credentials found, configuring Sonatype publishing")
} else {
    println("OSSRH credentials not found, skipping Sonatype configuration")
    println("To publish to Maven Central, set OSSRH_USERNAME and OSSRH_PASSWORD environment variables")
}

// Add custom tasks for debugging and validation
tasks.register("validateCredentials") {
    group = "verification"
    description = "Validates publishing credentials and configuration"
    
    doLast {
        println("=== Publishing Credentials Validation ===")
        println("OSSRH Username set: ${if (!ossrhUsername.isNullOrEmpty()) "✓" else "✗"}")
        println("OSSRH Password set: ${if (!ossrhPassword.isNullOrEmpty()) "✓" else "✗"}")
        println("GPG Private Key set: ${if (!System.getenv("GPG_PRIVATE_KEY").isNullOrEmpty()) "✓" else "✗"}")
        println("GPG Passphrase set: ${if (!System.getenv("GPG_PASSPHRASE").isNullOrEmpty()) "✓" else "✗"}")
        
        val stagingProfileId = project.findProperty("sonatypeStagingProfileId") as String? ?: System.getenv("SONATYPE_STAGING_PROFILE_ID")
        println("Staging Profile ID set: ${if (!stagingProfileId.isNullOrEmpty()) "✓ ($stagingProfileId)" else "✗"}")
        
        if (!hasCredentials) {
            println("\n⚠️ Missing OSSRH credentials. Publishing to Maven Central will fail.")
            println("Set OSSRH_USERNAME and OSSRH_PASSWORD environment variables or gradle properties.")
        } else {
            println("\n✓ Basic credentials are configured")
        }
    }
}

tasks.register("publishLocalOnly") {
    group = "publishing"
    description = "Publishes only to local Maven repository (no remote publishing)"
    dependsOn(":lib:publishToMavenLocal")
}

// Nexus publishing configuration
nexusPublishing {
    repositories {
        if (hasCredentials) {
            sonatype {
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
                username.set(ossrhUsername)
                password.set(ossrhPassword)
                // Package group for jp.vemi artifacts
                packageGroup.set("jp.vemi")
                // Set staging profile ID to avoid 401 errors during profile discovery
                // This can be obtained from: https://s01.oss.sonatype.org/#stagingProfiles
                stagingProfileId.set(project.findProperty("sonatypeStagingProfileId") as String? ?: System.getenv("SONATYPE_STAGING_PROFILE_ID"))
            }
        }
    }
    // Configuration for timeouts and retries
    transitionCheckOptions {
        maxRetries.set(60)
        delayBetween.set(java.time.Duration.ofSeconds(10))
    }
    // Increase timeouts for slower connections
    connectTimeout.set(java.time.Duration.ofMinutes(3))
    clientTimeout.set(java.time.Duration.ofMinutes(3))
}
import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

// OSSRH Configuration (s01.oss.sonatype.org)
// ==========================================
// This project uses OSSRH (Sonatype OSSRH s01) for publishing to Maven Central.
// 
// Required environment variables for automated publishing (priority order):
// 1. Preferred: OSSRH_USERNAME / OSSRH_PASSWORD
// 2. Fallback: CENTRAL_PORTAL_USERNAME / CENTRAL_PORTAL_PASSWORD
// 
// Setup guide: https://central.sonatype.org/publish/publish-guide/#deployment

nexusPublishing {
    repositories {
        sonatype {
            // OSSRH s01 configuration via nexus-publish-plugin
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            
            // Priority: OSSRH_* -> CENTRAL_PORTAL_* -> empty string
            username.set(System.getenv("OSSRH_USERNAME") 
                ?: System.getenv("CENTRAL_PORTAL_USERNAME") 
                ?: "")
            password.set(System.getenv("OSSRH_PASSWORD") 
                ?: System.getenv("CENTRAL_PORTAL_PASSWORD") 
                ?: "")
        }
    }
    
    // Configure timeouts for publishing
    connectTimeout.set(Duration.ofMinutes(3))
    clientTimeout.set(Duration.ofMinutes(3))
    
    // Transition check settings
    transitionCheckOptions {
        maxRetries.set(60)
        delayBetween.set(Duration.ofSeconds(10))
    }
}

// Add publishing tasks for validation
tasks.register("checkCentralPortalCredentials") {
    group = "verification"
    description = "Validates OSSRH/Central Portal publishing configuration"
    
    doLast {
        // Check OSSRH credentials (preferred)
        val ossrhUsername = System.getenv("OSSRH_USERNAME")
        val ossrhPassword = System.getenv("OSSRH_PASSWORD")
        
        // Check Central Portal credentials (fallback)
        val portalUsername = System.getenv("CENTRAL_PORTAL_USERNAME")
        val portalPassword = System.getenv("CENTRAL_PORTAL_PASSWORD")
        
        println("=== Publishing Configuration ===")
        println("OSSRH credentials (preferred):")
        println("  Username: ${if (!ossrhUsername.isNullOrEmpty()) "✓" else "✗"}")
        println("  Password: ${if (!ossrhPassword.isNullOrEmpty()) "✓" else "✗"}")
        println("")
        println("Central Portal credentials (fallback):")
        println("  Username: ${if (!portalUsername.isNullOrEmpty()) "✓" else "✗"}")
        println("  Password: ${if (!portalPassword.isNullOrEmpty()) "✓" else "✗"}")
        println("")
        
        val hasOssrhCreds = !ossrhUsername.isNullOrEmpty() && !ossrhPassword.isNullOrEmpty()
        val hasPortalCreds = !portalUsername.isNullOrEmpty() && !portalPassword.isNullOrEmpty()
        
        if (!hasOssrhCreds && !hasPortalCreds) {
            println("⚠️  Missing publishing credentials")
            println("Required environment variables (in priority order):")
            println("• OSSRH_USERNAME / OSSRH_PASSWORD (preferred)")
            println("• CENTRAL_PORTAL_USERNAME / CENTRAL_PORTAL_PASSWORD (fallback)")
            println("")
            println("📚 Setup guide: https://central.sonatype.org/publish/publish-guide/#deployment")
            println("📚 OSSRH account: https://issues.sonatype.org/")
        } else if (hasOssrhCreds) {
            println("✅ OSSRH credentials configured (using preferred)")
            println("Target: https://s01.oss.sonatype.org/")
            println("Ready for automated publishing!")
        } else {
            println("✅ Central Portal credentials configured (using fallback)")
            println("Target: https://s01.oss.sonatype.org/")
            println("💡 Consider migrating to OSSRH_* environment variables")
            println("Ready for automated publishing!")
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


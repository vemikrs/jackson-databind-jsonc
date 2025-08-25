plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

// Nexus publishing configuration
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME"))
            password.set(project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD"))
            // Package group for jp.vemi artifacts
            packageGroup.set("jp.vemi")
            // Optional: Set staging profile ID if known (helps avoid some 401 errors)
            // stagingProfileId.set("your-profile-id-here")
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
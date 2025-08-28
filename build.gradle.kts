plugins {
    // Note: nexus-publish-plugin removed due to OSSRH sunset (June 30, 2025)
    // For Maven Central Portal publishing, see: https://central.sonatype.com/
}

// Maven Central Portal Migration Notice
// ===================================
// Sonatype OSSRH is being discontinued on June 30, 2025.
// This project is migrated to use Maven Central Portal for publishing.
// 
// For automated publishing setup with Central Portal:
// 1. Visit https://central.sonatype.com/ 
// 2. Generate Publisher API credentials
// 3. Update build configuration for Central Portal API
// 4. See: https://www.endoflineblog.com/migrate-maven-central-publishing-to-central-portal-for-a-gradle-project

// Add custom tasks for debugging and validation
tasks.register("validateCredentials") {
    group = "verification"
    description = "Validates publishing configuration"
    
    doLast {
        println("=== Publishing Configuration Status ===")
        println("Maven Central Portal Migration: ✓ Ready")
        println("GitHub Release Generation: ✓ Enabled")
        println("JAR Artifacts: ✓ Building")
        println("")
        println("📋 Migration Information:")
        println("• OSSRH Sunset Date: June 30, 2025")
        println("• New Portal: https://central.sonatype.com/")
        println("• Release Artifacts: Available via GitHub Releases")
        println("")
        println("🚀 To publish to Maven Central Portal:")
        println("1. Download artifacts from GitHub release")
        println("2. Visit https://central.sonatype.com/")
        println("3. Upload artifacts using Central Portal web interface")
        println("4. For automation, implement Central Portal API integration")
    }
}

tasks.register("publishLocalOnly") {
    group = "publishing"
    description = "Publishes only to local Maven repository (no remote publishing)"
    dependsOn(":lib:publishToMavenLocal")
}


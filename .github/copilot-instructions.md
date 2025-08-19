# Jackson-Databind-Jsonc Development Guide

Jackson-Databind-Jsonc is a Java library that extends Jackson's `JsonMapper` to handle JSONC (JSON with Comments) format. The library provides `JsoncMapper` and `JsoncUtils` classes to parse JSON files with comments while maintaining full Jackson compatibility.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites and Setup
- Set up Java 21 (REQUIRED - project uses Java 21 toolchain):
  - `export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64`
  - `export PATH=$JAVA_HOME/bin:$PATH`
  - Verify with `java -version` - should show Java 21
- Set execute permission: `chmod +x ./gradlew`

### Build and Test
- Build the project:
  - `./gradlew build` -- Initial build takes approximately 28 seconds (includes Gradle setup). NEVER CANCEL. Set timeout to 60+ seconds.
  - Subsequent builds take ~2 seconds
- Run tests:
  - `./gradlew test` -- Takes under 1 second. All 14 tests should pass.
  - Test reports available in `lib/build/reports/tests/test/index.html`
- Clean build:
  - `./gradlew clean build` -- Takes ~2 seconds for fresh build
- Create JAR:
  - `./gradlew jar` -- Creates `lib/build/libs/lib-1.0.0.jar`
  - DO NOT use `./gradlew shadowJar` -- this task has known issues and will fail

### Testing and Validation
- ALWAYS run the full test suite after making code changes: `./gradlew test`
- ALWAYS ensure builds complete successfully: `./gradlew build`
- Manual validation scenario: Verify JSONC parsing works correctly by ensuring tests in `JsoncMapperTest` and `JsoncUtilsTest` pass
- Security validation: Ensure `SecurityTest` passes - it validates ReDoS protection and malformed comment handling

## Project Structure

### Key Directories and Files
```
/
├── lib/                           # Main library project
│   ├── build.gradle.kts          # Build configuration
│   └── src/
│       ├── main/java/jp/vemi/jsoncmapper/
│       │   ├── JsoncMapper.java   # Main mapper class extending Jackson's JsonMapper
│       │   └── JsoncUtils.java    # Utility class for comment removal
│       └── test/java/jp/vemi/jsoncmapper/
│           ├── JsoncMapperTest.java    # Core functionality tests
│           ├── JsoncUtilsTest.java     # Utility function tests
│           └── SecurityTest.java       # Security and ReDoS protection tests
├── gradle/
│   ├── libs.versions.toml        # Dependency version catalog
│   └── wrapper/                  # Gradle wrapper files
├── .github/workflows/release.yml # CI/CD pipeline configuration
├── settings.gradle.kts           # Multi-project build settings
├── README.md                     # English documentation
└── README_ja.md                  # Japanese documentation
```

### Core Classes Location
- `JsoncMapper`: `lib/src/main/java/jp/vemi/jsoncmapper/JsoncMapper.java`
- `JsoncUtils`: `lib/src/main/java/jp/vemi/jsoncmapper/JsoncUtils.java`
- All tests: `lib/src/test/java/jp/vemi/jsoncmapper/`

## Dependencies and Configuration

### Main Dependencies
- Jackson Databind: 2.18.4 (core dependency)
- Apache Commons Math: 3.6.1
- Google Guava: 33.4.0-jre
- JUnit Jupiter: 5.11.4 (testing)

### Gradle Configuration
- Uses Gradle 8.10.2 with wrapper
- Java 21 toolchain required
- Shadow plugin configured (but shadowJar task has issues)
- Multi-project build with single `lib` module

## Development Workflows

### Making Code Changes
1. ALWAYS ensure Java 21 is set up before starting
2. Make your changes to source files in `lib/src/main/java/jp/vemi/jsoncmapper/`
3. Update or add tests in `lib/src/test/java/jp/vemi/jsoncmapper/`
4. Run tests immediately: `./gradlew test`
5. Run full build to ensure everything works: `./gradlew build`
6. Check that the JAR builds successfully: `./gradlew jar`

### Common Development Tasks
- View dependency tree: `./gradlew :lib:dependencies --configuration runtimeClasspath`
- Generate Javadoc: `./gradlew javadoc`
- Clean all build artifacts: `./gradlew clean`
- List all available tasks: `./gradlew tasks`

### Critical Files to Check After Changes
- Always review test results after modifying `JsoncUtils.java` or `JsoncMapper.java`
- Check `SecurityTest.java` when making changes to comment parsing logic
- Verify `JsoncUtilsTest.java` passes when modifying utility functions

## Testing Strategy

### Test Categories
1. **Core Functionality** (`JsoncMapperTest`): Basic JSONC parsing and mapping
2. **Utility Functions** (`JsoncUtilsTest`): Comment removal logic and edge cases
3. **Security** (`SecurityTest`): ReDoS protection and malformed input handling

### Test Data Scenarios
- Basic comment removal: `/* comment */ { "key": "value" }`
- Multi-line comments: Comments spanning multiple lines
- Comments in strings: Ensuring comments inside JSON strings are preserved
- Malformed comments: Unclosed comments and edge cases
- ReDoS attack patterns: Protection against Regular Expression Denial of Service

## CI/CD and Release

### GitHub Actions
- Release workflow: `.github/workflows/release.yml`
- Triggers on version tags (`v*.*.*`)
- Requires Java 21 setup
- Runs `./gradlew build` then creates releases with JAR artifacts

### Build Timing Expectations
- NEVER CANCEL: Initial Gradle download and build takes up to 28 seconds
- Subsequent builds: ~2 seconds
- Tests: <1 second
- Clean builds: ~2 seconds
- JAR creation: <1 second

## Known Issues and Limitations

### Build Issues
- `./gradlew shadowJar` fails with ZIP file creation errors - use `./gradlew jar` instead
- Project name mismatch in settings.gradle.kts ("jackson-databind-jasonc" vs repository name)

### Feature Limitations
- End-of-line comments (`//`) are not fully supported (commented out tests)
- Nested comments are not supported (commented out tests)
- Only block comments (`/* */`) are reliably supported

## Common Commands Reference

```bash
# Setup (run once)
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
chmod +x ./gradlew

# Development workflow
./gradlew clean build    # Clean and build (2 seconds)
./gradlew test          # Run tests (<1 second)
./gradlew jar           # Create JAR artifact

# Validation
./gradlew build         # Full build validation
./gradlew check         # Run all checks

# Information
./gradlew tasks         # List available tasks
./gradlew :lib:dependencies --configuration runtimeClasspath  # View dependencies
```

## Directory Listing Reference

### Repository Root
```
.git/
.gitattributes
.github/
  workflows/
    release.yml
.gitignore
.gradle/
.vscode/
  settings.json
LICENSE
README.md
README_ja.md
gradle/
  libs.versions.toml
  wrapper/
gradlew
gradlew.bat
lib/
  build.gradle.kts
  src/
settings.gradle.kts
```

### Library Source Structure
```
lib/src/main/java/jp/vemi/jsoncmapper/
├── JsoncMapper.java      # Main API class
└── JsoncUtils.java       # Comment parsing utilities

lib/src/test/java/jp/vemi/jsoncmapper/
├── JsoncMapperTest.java  # Integration tests
├── JsoncUtilsTest.java   # Unit tests
└── SecurityTest.java     # Security tests
```
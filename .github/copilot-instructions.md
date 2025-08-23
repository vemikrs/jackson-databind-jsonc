# Jackson-Databind-Jsonc Development Guide

Jackson-Databind-Jsonc is a Java library that extends Jackson's `JsonMapper` to handle JSONC (JSON with Comments) format. The library provides `JsoncMapper` and `JsoncUtils` classes to parse JSON files with comments while maintaining full Jackson compatibility.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Quick Start Summary
- **Language**: Java 21 (required, toolchain enforced)
- **Build System**: Gradle 8.10.2 with wrapper
- **Main Classes**: `JsoncMapper` (extends Jackson's JsonMapper), `JsoncUtils` (comment removal utility)
- **Test Framework**: JUnit Jupiter 5.11.4 (14 tests total)
- **Core Dependencies**: Jackson Databind 2.18.4, Guava 33.4.0-jre, Apache Commons Math 3.6.1

## Coding Patterns and Conventions

### Architecture Overview
- **JsoncMapper**: Main API class that extends Jackson's JsonMapper to add JSONC support
- **JsoncUtils**: Static utility class for secure comment removal with ReDoS protection
- **Design Pattern**: Decorator pattern - JsoncMapper wraps standard JsonMapper functionality

### Code Style and Conventions
- **Input Validation**: All public methods validate null inputs and throw IllegalArgumentException
- **Security First**: ReDoS protection implemented in comment parsing with linear-time algorithms
- **Error Handling**: Use specific exception types (JsonProcessingException, JsonMappingException, IllegalArgumentException)
- **Documentation**: Comprehensive Javadoc for all public methods with @param, @return, @throws
- **String Handling**: Use StringBuilder for efficient string building, preserve original string lengths where possible

### Key Implementation Details
- **Comment Parsing**: State machine approach tracking string context to avoid removing comments inside JSON strings
- **Method Overrides**: Override all relevant Jackson JsonMapper methods (readValue, readTree) to maintain API compatibility
- **Resource Management**: Proper try-with-resources for File/InputStream operations
- **Character Encoding**: Use StandardCharsets.UTF_8 for file operations

### Testing Patterns
- **Test Classes**: Separate test classes for each main class (JsoncMapperTest, JsoncUtilsTest, SecurityTest)
- **Test Data**: Use inline JSON strings for simple tests, @TempDir for file-based tests
- **Security Tests**: Include ReDoS protection tests with @Timeout annotations
- **Edge Cases**: Test null inputs, empty strings, malformed comments, nested scenarios


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
- Tests: <1 second (runs 14 tests across 3 test classes)
- Clean builds: ~2 seconds
- JAR creation: <1 second
- Fat JAR creation: ~1 second (custom fatJar task)

## Known Issues and Limitations

### Build Issues
- `./gradlew shadowJar` fails with ZIP file creation errors - use `./gradlew jar` instead
- Custom `fatJar` task created as shadowJar replacement due to Java 21 compatibility issues
- Project name mismatch in settings.gradle.kts ("jackson-databind-jasonc" vs repository name)

### Gradle Tasks
- `./gradlew fatJar` - Creates fat JAR with all dependencies (recommended over shadowJar)
- `./gradlew shadowJar` - Legacy compatibility task that copies fatJar output
- Build task automatically depends on fatJar task

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
./gradlew fatJar        # Create fat JAR with dependencies

# Validation
./gradlew build         # Full build validation
./gradlew check         # Run all checks

# Information
./gradlew tasks         # List available tasks
./gradlew :lib:dependencies --configuration runtimeClasspath  # View dependencies
```

## Common Development Scenarios

### Adding a New JSONC Feature
```bash
# 1. Create test case first (TDD approach)
# Edit lib/src/test/java/jp/vemi/jsoncmapper/JsoncUtilsTest.java or JsoncMapperTest.java

# 2. Run tests to see failure
./gradlew test

# 3. Implement feature in JsoncUtils.java or JsoncMapper.java
# 4. Run tests to verify fix
./gradlew test

# 5. Run full build
./gradlew build
```

### Adding New Comment Format Support
```java
// Example: Adding support for // line comments
// 1. Add test case in JsoncUtilsTest.java:
@Test
public void testLineComments() {
    String jsonc = "{ \"key\": \"value\" // comment\n}";
    String expected = "{ \"key\": \"value\" \n}";
    String result = JsoncUtils.removeComments(jsonc);
    assertEquals(expected, result);
}

// 2. Implement in JsoncUtils.removeComments() method
// 3. Consider security implications (ReDoS protection)
// 4. Add corresponding test in SecurityTest.java
```

### Debugging Comment Parsing Issues
```bash
# Run specific test class
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncUtilsTest"

# Run single test method
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncUtilsTest.testRemoveComments"

# Debug with verbose output
./gradlew test --info --tests "jp.vemi.jsoncmapper.SecurityTest"
```

### Performance Testing
```java
// Add to SecurityTest.java for ReDoS protection
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
public void testPerformanceWithLargeInput() {
    StringBuilder largeInput = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
        largeInput.append("/* comment ").append(i).append(" */ ");
    }
    largeInput.append("{ \"key\": \"value\" }");
    
    String result = JsoncUtils.removeComments(largeInput.toString());
    assertNotNull(result);
    assertTrue(result.contains("\"value\""));
}
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

## Troubleshooting Guide

### Common Issues and Solutions

#### Java Version Issues
- **Problem**: Build fails with "Unsupported class file major version"
- **Solution**: Ensure Java 21 is installed and JAVA_HOME is set correctly
- **Check**: `java -version` should show Java 21.x.x

#### Gradle Permission Issues
- **Problem**: "./gradlew: Permission denied"
- **Solution**: `chmod +x ./gradlew`

#### Test Failures
- **SecurityTest timeout**: Usually indicates ReDoS vulnerability in comment parsing
- **File-based tests fail**: Check @TempDir usage and file path handling
- **JsoncMapperTest failures**: Verify comment removal logic in JsoncUtils

#### Build Performance
- **Slow initial build**: Normal for first run (28+ seconds for Gradle setup)
- **shadowJar failures**: Use `./gradlew fatJar` instead (Java 21 compatibility)
- **Out of memory**: Increase Gradle heap with `export GRADLE_OPTS="-Xmx2g"`

#### Development Workflow Issues
- **IDE not recognizing Java 21**: Configure IDE to use correct JDK
- **Tests not running**: Ensure JUnit platform launcher is in runtime classpath
- **JAR not working**: Use fatJar task for standalone executable JAR

### Debugging Tips
- Use `./gradlew test --info` for detailed test output
- Use `./gradlew build --debug` for comprehensive build debugging
- Check `lib/build/reports/tests/test/index.html` for test report details
- Use `./gradlew dependencies` to debug dependency conflicts

## GitHub Copilot and MCP Integration

### About This Instructions File
This `.github/copilot-instructions.md` file provides custom instructions to GitHub Copilot coding agents to make them more effective when working with the jackson-databind-jsonc project. It follows GitHub's recommended practices for Copilot custom instructions.

### What Makes Copilot Smarter Here
1. **Project-Specific Context**: Detailed information about build system, dependencies, and architecture
2. **Common Patterns**: Documentation of coding conventions and design patterns used
3. **Troubleshooting Guide**: Solutions to common issues developers encounter
4. **Performance Expectations**: Realistic timing expectations for builds and tests
5. **Security Considerations**: Emphasis on ReDoS protection and input validation

### Model Context Protocol (MCP) Servers
For advanced Copilot functionality, consider setting up MCP servers for:
- **Code Analysis**: Enhanced understanding of Jackson framework integration
- **Security Scanning**: Automated detection of potential ReDoS vulnerabilities
- **Performance Monitoring**: Build time and test execution tracking
- **Dependency Management**: Automated dependency conflict resolution

### Copilot Best Practices for This Project
- Always run tests after making changes: `./gradlew test`
- Use the established error handling patterns (IllegalArgumentException for null inputs)
- Follow the existing security-first approach in comment parsing
- Maintain API compatibility with Jackson's JsonMapper
- Write comprehensive Javadoc for all public methods

### Extending These Instructions
When adding new features or patterns:
1. Update the relevant sections in this file
2. Add examples to "Common Development Scenarios"
3. Include troubleshooting steps for new issues
4. Document any new dependencies or build requirements
5. Update the Quick Start Summary if architecture changes
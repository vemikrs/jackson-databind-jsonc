Language: [日本語](./README.md) | English

# Jackson-Databind-Jsonc

This project extends Jackson's `JsonMapper` by adding a `JsoncMapper` to handle JSONC (JSON with Comments).

## Features

- Supports JSONC with block comments (`/* */`) and end-of-line comments (`//`)
- Extends Jackson's `JsonMapper`
- Multi-version Java support (Java 8, 11, 17, 21, 24)
- Dual distribution strategy for different deployment scenarios

## Requirements

### Java Version Support

This library supports multiple Java versions:

- Java 8 (LTS) – minimum required
- Java 11 (LTS) – fully supported
- Java 17 (LTS) – fully supported
- Java 21 (LTS) – fully supported
- Java 24 – supported (when available)

For maximum compatibility, classes are compiled to Java 8 bytecode (major version 52) and built/tested across all supported Java versions.

### Testing Approach

- Build Testing: Built and tested with Gradle (requires Java 17+)
- Runtime Compatibility: Generated JAR verified on Java 8+
- Bytecode Target: All classes compiled to Java 8 bytecode

## Distribution Options

To meet different environment requirements, two distribution formats are provided.

### Slim JAR (Recommended)
- File: `lib-1.0.0.jar` (~5KB)
- Use Case: Modern Jackson environments, Maven/Gradle projects
- Dependencies: Requires Jackson 2.18.4+
- Benefits: Lightweight, flexible dependency management

### All-in-One JAR (Enterprise)
- File: `lib-1.0.0-all.jar` (~7.8MB)
- Use Case: Enterprise Java apps, legacy environments, dependency conflict avoidance
- Dependencies: Self-contained (Jackson included)
- Benefits: Single-file deployment, no dependency conflicts

## Installation

### Maven (Slim JAR)
```xml
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle (Slim JAR)
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.0'
```

### Manual Installation
1. Download the JAR from the [releases page](https://github.com/vemic/jackson-databind-jsonc/releases)
2. Add it to your project's classpath

## When to Use Which JAR

### Choose Slim JAR when:
- ✅ Jackson 2.18.x+ is available
- ✅ Dependencies are managed with Maven/Gradle
- ✅ No conflicts with other libraries
- ✅ Minimal JAR size is desired

### Choose All-in-One JAR when:
- ✅ Enterprise Java application environment
- ✅ Jackson versions are fixed
- ✅ You need to avoid dependency conflicts
- ✅ Single JAR deployment is required
- ✅ You want to avoid complex dependency management

## Usage

### Basic Usage (Both JARs)
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Example {
    public static void main(String[] args) {
        // JSONC supports both block and end-of-line comments
        String jsonWithComments = """
            {
                /* Block comment */
                "name": "example", // End-of-line comment
                "value": 42
            }
            """;
        JsoncMapper mapper = new JsoncMapper();
        
        try {
            MyClass obj = mapper.readValue(jsonWithComments, MyClass.class);
            System.out.println(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
```

### Spring Boot Environment (Slim JAR)
```java
import jp.vemi.jsoncmapper.JsoncMapper;

@Component
public class ModernJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

### Enterprise Java Application (All-in-One JAR)
```java
import jp.vemi.jsoncmapper.JsoncMapper;

public class IntraMartJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

## Environment-Specific Recommendations

### Modern Spring Boot
- Recommended: Slim JAR
- Reason: Spring Boot manages the latest Jackson

### Traditional Web Application Servers
- Recommended: All-in-One JAR
- Reason: Avoid conflicts with server-side Jackson

### Enterprise Java Environments
- Recommended: All-in-One JAR
- Reason: Enterprise platforms often fix specific Jackson versions

### Microservices
- Recommended: Slim JAR
- Reason: Container size optimization and dependency injection

## Development

### Building and Testing
This project uses Gradle. Java 21 is required to build.

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncUtilsTest"
./gradlew test --tests "jp.vemi.jsoncmapper.SecurityTest"
./gradlew test --tests "jp.vemi.jsoncmapper.JsoncMapperTest"
```

### GitHub Workflows

#### PR Test Execution
A workflow is included to run tests manually or automatically on PR events.

Manual Execution:
1. Go to the Actions tab
2. Select "PR Test Execution"
3. Click "Run workflow"
4. Configure options:
   - Test Scope: `all`, `unit-only`, `security-only`, or `integration-only`
   - Run Build: Enable/disable full build before tests
   - PR Number: Optionally specify a PR

Automatic Execution:
Runs on PR opened/synchronize/ready_for_review events with full scope.

Features:
- Selective test execution
- Detailed results and summaries
- PR comments with results
- Gradle caching for faster runs
- Timeout protection (10 minutes)

## Troubleshooting

### Dependency Conflict Errors
1. Switch to All-in-One JAR
2. Or use exclusion settings to resolve Jackson conflicts

### ClassNotFoundException
1. Slim JAR: Verify Jackson 2.18.4 dependency
2. All-in-One JAR: Verify JAR is on the classpath

### Performance Considerations
- Slim JAR: Optimized startup time
- All-in-One JAR: Slightly increased memory usage possible
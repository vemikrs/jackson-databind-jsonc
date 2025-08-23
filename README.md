# Jackson-Databind-Jsonc

This project extends Jackson's `JsonMapper` by adding a new `JsoncMapper` to handle JSONC (JSON with Comments) format.

## Features

- Supports JSONC format
- Extends Jackson's `JsonMapper`

## Installation

### Add a dependency

You need to install jackson-databind.  
The version is optional.

#### Using Maven:
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

#### Using Gradle:
```groovy
implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.0'
```

### Adding jar file
1. Download the latest jar file from the releases page.
1. Add the jar file to your project's classpath.

## Usage
To use JsoncMapper:
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Example {
    public static void main(String[] args) {
        String jsonWithComments = "/* This is a comment */ { \"key\": \"value\" }";
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

## Development

### Building and Testing
This project uses Gradle for building and testing. Java 21 is required.

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
The repository includes an optional test execution workflow that can be triggered manually or automatically on PR events.

**Manual Execution:**
1. Go to Actions tab in GitHub
2. Select "PR Test Execution" workflow
3. Click "Run workflow"
4. Configure options:
   - **Test Scope**: Choose from `all`, `unit-only`, `security-only`, or `integration-only`
   - **Run Build**: Enable/disable full build before tests
   - **PR Number**: Optionally specify a PR number to test

**Automatic Execution:**
The workflow automatically runs on PR events (opened, synchronize, ready_for_review) with full test scope.

**Features:**
- Selective test execution by scope
- Detailed test results with summaries
- PR comments with test results
- Gradle caching for faster execution
- Timeout protection (10 minutes max)

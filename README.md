# Jackson-Databind-Jsonc

This project extends Jackson's `JsonMapper` by adding a new `JsoncMapper` to handle JSONC (JSON with Comments) format.

## Features

- Supports JSONC format
- Extends Jackson's `JsonMapper`
- Multi-version Java support (Java 8, 11, 17, 21, 24)

## Requirements

### Java Version Support

This library supports multiple Java versions:

- **Java 8** (LTS) - Minimum required version
- **Java 11** (LTS) - Fully supported
- **Java 17** (LTS) - Fully supported  
- **Java 21** (LTS) - Fully supported
- **Java 24** - Supported (when available)

The library is compiled to Java 8 bytecode for maximum compatibility while being built and tested across all supported Java versions.

### Testing Approach

- **Build Testing**: The project is built and tested with Java 17+ (required by Gradle)
- **Runtime Compatibility**: The generated JAR is tested for compatibility with Java 8+
- **Bytecode Target**: All classes are compiled with Java 8 bytecode (major version 52)

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

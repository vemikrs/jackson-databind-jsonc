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

# Jackson-Databind-Jsonc

This project extends Jackson's `JsonMapper` by adding a new `JsoncMapper` to handle JSONC (JSON with Comments) format.

## Features

- Extends Jackson's `JsonMapper`
- Supports JSONC format

## Usage

To use `JsoncMapper`:

```java
import com.fasterxml.jackson.databind.json.JsoncMapper;

public class Example {
    public static void main(String[] args) {
        JsoncMapper mapper = new JsoncMapper();
        // Your code here
    }
}

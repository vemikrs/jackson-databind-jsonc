Language: [日本語](./README.md) | English

# Jackson-Databind-Jsonc

[![Maven Central](https://img.shields.io/maven-central/v/jp.vemi/jackson-databind-jsonc.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/jp.vemi/jackson-databind-jsonc)
[![javadoc](https://javadoc.io/badge2/jp.vemi/jackson-databind-jsonc/latest/javadoc.svg)](https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/latest/)
[![Release Workflow](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml/badge.svg?branch=main)](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml)

This project extends Jackson's `JsonMapper` by adding a `JsoncMapper` to handle JSONC (JSON with Comments).

## Features

- Supports JSONC with block comments (`/* */`) and end-of-line comments (`//`)
- Optional JSON5 features via Builder pattern
  - Single-quoted strings (`'text'` → `"text"`)
  - Hexadecimal numbers (`0xFF` → `255`)
  - Plus sign numbers (`+123` → `123`)
  - Infinity and NaN literals (`Infinity`/`NaN` → `null`)
  - Multiline strings and unescaped control characters
- Extends Jackson's `JsonMapper`
- Multi-version Java support (Java 8, 11, 17, 21, 24)
- Dual distribution strategy (Slim / All-in-One)
- Protection against ReDoS attacks (linear-time algorithms)
- Optional trailing comma removal

## Supported Comment Formats

### ✅ Fully Supported
- Block comments: `/* comment */` (multi-line)
- End-of-line comments: `// comment` (to end of line)
- Multi-line comments
- String protection (comment-like content inside strings is preserved)

```javascript
{
  /* Main configuration section */
  "database": {
    "host": "localhost", // Default host
    "port": 5432,
    /* Multi-line comment
       with detailed
       database configuration */
    "timeout": 30
  },
  "message": "This /* is not removed */ from string" // String content protected
}
```

### ❌ Unsupported
- Nested comments: `/* outer /* inner */ outer */`
- Other JSON5 features (e.g., unquoted object keys)

### 🔧 Optional JSON5 Features (via Builder)

- Trailing comma removal
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .build();
```

- Single-quoted strings
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowSingleQuotes(true)
    .build();
// Input: { 'key': 'value' }
// Output: { "key": "value" }
```

- Hexadecimal numbers
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowHexNumbers(true)
    .build();
// Input: { "value": 0xFF }
// Output: { "value": 255 }
```

- Plus sign numbers
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowPlusNumbers(true)
    .build();
// Input: { "value": +123 }
// Output: { "value": 123 }
```

- Infinity and NaN
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowInfinityAndNaN(true)
    .build();
// Input: { "inf": Infinity, "nan": NaN }
// Output: { "inf": null, "nan": null }
```

- Multiline strings / control chars
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowMultilineStrings(true)
    .allowUnescapedControlChars(true)
    .build();
```

- All features combined
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .allowSingleQuotes(true)
    .allowHexNumbers(true)
    .allowPlusNumbers(true)
    .allowInfinityAndNaN(true)
    .allowMultilineStrings(true)
    .allowUnescapedControlChars(true)
    .build();
```

## Requirements

- Java 8+ runtime compatible
- Build requires Java 21 (bytecode targets Java 8 via `--release 8`)

## Distribution Options

### Slim JAR (Recommended)
- File: `jackson-databind-jsonc-<version>.jar` (~5KB)
- Use case: modern Jackson environments, Maven/Gradle projects
- Dependency: Jackson Databind 2.20.0+ required
- Maven: `jp.vemi:jackson-databind-jsonc:<version>`
- Gradle: `implementation("jp.vemi:jackson-databind-jsonc:<version>")`

### All-in-One JAR (Enterprise)
- File: `jackson-databind-jsonc-<version>-all.jar` (~7.8MB)
- Use case: enterprise/legacy environments, dependency conflict avoidance
- Self-contained (includes Jackson)
- Maven: `jp.vemi:jackson-databind-jsonc-all:<version>`
- Gradle: `implementation("jp.vemi:jackson-databind-jsonc-all:<version>")`

## Installation

> Latest versions are available from Maven Central (automated via Central Portal)

### Maven (Slim JAR)
```xml
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc</artifactId>
  <version>1.0.5</version>
</dependency>
```

### Gradle (Slim JAR)
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.5'
```

### Maven (All-in-One JAR)
```xml
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc-all</artifactId>
  <version>1.0.5</version>
</dependency>
```

### Gradle (All-in-One JAR)
```groovy
implementation 'jp.vemi:jackson-databind-jsonc-all:1.0.5'
```

### Manual Installation
1. Download from [Releases](https://github.com/vemikrs/jackson-databind-jsonc/releases)
2. Add to your project's classpath

**📋 Release Information:**
- Automated publishing via Maven Central Portal
- Manual download available from GitHub Releases
- Details: [PUBLISHING.md](./PUBLISHING.md)

## API Documentation

- Latest (javadoc.io): https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/latest/
- Versioned example: https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/1.0.5/
- Local generation: run `./gradlew javadoc`, then open `lib/build/docs/javadoc/index.html`

## When to Use Which JAR

### Choose Slim JAR when
- ✅ Jackson 2.20.0+ is available
- ✅ You manage dependencies with Maven/Gradle
- ✅ No conflicts with other libraries
- ✅ You want a minimal JAR size

### Choose All-in-One JAR when
- ✅ Enterprise Java environments
- ✅ Existing Jackson version is fixed
- ✅ You want to avoid dependency conflicts
- ✅ Single JAR distribution required
- ✅ You prefer avoiding complex dependency management

## Quick Usage

```java
JsoncMapper mapper = new JsoncMapper();
String jsonWithComments = """
{
  /* Block comment */
  "name": "example", // End-of-line comment
  "value": 42
}
""";
MyClass obj = mapper.readValue(jsonWithComments, MyClass.class);
```

## Performance

- Normal sizes (< 10MB): fast; memory ~1.5x of input size
- Large files (> 10MB): pre-remove comments is recommended

## Security

- Linear-time algorithms, no regex → ReDoS resistant
- Null input throws `IllegalArgumentException`
- Comment-like text inside strings is preserved

## Development

### Build & Test
- Build requires Java 21 (bytecode targets Java 8 via `--release 8`)

```bash
./gradlew build
./gradlew test
```

### CI / Build & Test Status
- Release Workflow: see badges above or [Actions > Release](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml)
- Local test report: `lib/build/reports/tests/test/index.html`
- Typical GitHub Actions view:
  - Actions tab → select workflow → open a run → check Summary for success/failure
  - Download uploaded artifacts (JARs, etc.) as needed

## Troubleshooting (excerpt)

### Dependency conflicts
```xml
<!-- For Maven (All-in-One is a separate artifact) -->
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc-all</artifactId>
  <version>1.0.5</version>
</dependency>
```

### ClassNotFoundException
- Slim JAR: verify Jackson 2.20.0+ dependency
- All-in-One JAR: ensure the single JAR is on the classpath

### Build
```bash
java -version # ensure Java 21
export JAVA_HOME=/path/to/java21
./gradlew clean build --refresh-dependencies
```

## Resources
- [MIGRATION_NOTES.md](./MIGRATION_NOTES.md)
- [PUBLISHING.md](./PUBLISHING.md)
- [SECURITY.md](./SECURITY.md)
- [LICENSE](./LICENSE)
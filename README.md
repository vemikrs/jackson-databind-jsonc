# Jackson-Databind-Jsonc

This project extends Jackson's `JsonMapper` by adding a new `JsoncMapper` to handle JSONC (JSON with Comments) format.

## Features

- Supports JSONC format
- Extends Jackson's `JsonMapper`
- Dual distribution strategy for different deployment scenarios

## Distribution Options

This library provides two distribution formats to meet different environment requirements:

### Slim JAR (Recommended)
- **File**: `lib-1.0.0.jar` (~5KB)
- **Use Case**: Modern Jackson environments, Maven/Gradle projects
- **Dependencies**: Requires Jackson 2.18.4+
- **Benefits**: Lightweight, flexible dependency management

### All-in-One JAR (Enterprise)
- **File**: `lib-1.0.0-all.jar` (~7.8MB) 
- **Use Case**: Enterprise Java applications, legacy environments, dependency conflict avoidance
- **Dependencies**: Self-contained (Jackson included)
- **Benefits**: Single file deployment, no dependency conflicts

## Installation

### Maven Dependency (Slim JAR)
```xml
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle Dependency (Slim JAR)
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.0'
```

### Manual Installation
1. Download the appropriate JAR file from the [releases page](https://github.com/vemic/jackson-databind-jsonc/releases)
2. Add the JAR file to your project's classpath

## When to Use Which JAR

### Choose Slim JAR when:
✅ Jackson 2.18.x+ is available  
✅ Dependency management is possible (Maven/Gradle)  
✅ No conflicts with other libraries  
✅ Minimal JAR size is desired  

### Choose All-in-One JAR when:
✅ Enterprise Java application environment  
✅ Legacy Jackson versions are fixed  
✅ Dependency conflicts need to be avoided  
✅ Single JAR deployment is required  
✅ Complex dependency management should be avoided  

## Usage

### Basic Usage (Both JARs)
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

### Enterprise Java Application Environment (All-in-One JAR)
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
- **Recommended**: Slim JAR
- **Reason**: Spring Boot manages latest Jackson

### Traditional Web Application Servers  
- **Recommended**: All-in-One JAR
- **Reason**: Avoids conflicts with server-side Jackson

### Enterprise Java Application Environment
- **Recommended**: All-in-One JAR  
- **Reason**: Enterprise platforms often fix specific Jackson versions

### Microservices
- **Recommended**: Slim JAR
- **Reason**: Container size optimization and dependency injection

## Troubleshooting

### Dependency Conflict Errors
1. Switch to All-in-One JAR
2. Or use exclusion settings to resolve Jackson conflicts

### ClassNotFoundException
1. Slim JAR: Verify Jackson 2.18.4 dependency
2. All-in-One JAR: Verify JAR is in classpath

### Performance Considerations
- Slim JAR: Optimized startup time
- All-in-One JAR: Slightly increased memory usage possible

---

# 使用ガイド (Japanese)

## 配布形式

このライブラリは、異なる環境のニーズに対応するため、2つの配布形式を提供しています。

### Slim JAR（推奨）
- **ファイル**: `lib-1.0.0.jar` （約5KB）
- **用途**: 最新Jackson環境、Maven/Gradleプロジェクト
- **依存関係**: Jackson 2.18.4が必要
- **特徴**: 軽量で依存関係の管理が柔軟

### All-in-One JAR（エンタープライズ向け）
- **ファイル**: `lib-1.0.0-all.jar` （約7.8MB）
- **用途**: エンタープライズJavaアプリケーション、レガシー環境、依存関係競合回避
- **依存関係**: 自己完結（Jackson内蔵）
- **特徴**: 単一ファイルで完全に動作、依存関係競合なし

## エンタープライズ環境での選択指針

### Slim JARを選ぶ場合
✅ Jackson 2.18.x以上が使用可能  
✅ 依存関係管理が可能（Maven/Gradle）  
✅ 他ライブラリとの競合がない  
✅ JAR サイズを最小限に抑えたい  

### All-in-One JARを選ぶ場合
✅ エンタープライズJavaアプリケーション環境  
✅ 古いJacksonバージョンが固定されている環境  
✅ 依存関係競合を避けたい  
✅ 単一JARでの配布が必要  
✅ 複雑な依存関係管理を避けたい

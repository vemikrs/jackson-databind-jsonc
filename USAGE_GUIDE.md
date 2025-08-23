# Jackson-Databind-JSONC 使用ガイド

## 配布形式

このライブラリは、異なる環境のニーズに対応するため、2つの配布形式を提供しています。

### Slim JAR（推奨）
- **ファイル**: `lib-1.0.0.jar` （約5KB）
- **用途**: 最新Jackson環境、Maven/Gradleプロジェクト
- **依存関係**: Jackson 2.18.4が必要
- **特徴**: 軽量で依存関係の管理が柔軟

```xml
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.0</version>
</dependency>
```

### All-in-One JAR（エンタープライズ向け）
- **ファイル**: `lib-1.0.0-all.jar` （約7.8MB）
- **用途**: intra-mart、レガシー環境、依存関係競合回避
- **依存関係**: 自己完結（Jackson内蔵）
- **特徴**: 単一ファイルで完全に動作、依存関係競合なし

## エンタープライズ環境での選択指針

### Slim JARを選ぶ場合
✅ Jackson 2.18.x以上が使用可能  
✅ 依存関係管理が可能（Maven/Gradle）  
✅ 他ライブラリとの競合がない  
✅ JAR サイズを最小限に抑えたい  

### All-in-One JARを選ぶ場合
✅ intra-mart環境  
✅ 古いJacksonバージョンが固定されている環境  
✅ 依存関係競合を避けたい  
✅ 単一JARでの配布が必要  
✅ 複雑な依存関係管理を避けたい  

## 実装例

### Slim JAR使用時
```java
import jp.vemi.jsoncmapper.JsoncMapper;

public class ModernJsoncProcessor {
    private final JsoncMapper mapper = new JsoncMapper();
    
    public <T> T parseJsonc(String jsoncContent, Class<T> valueType) 
            throws JsonProcessingException {
        return mapper.readValue(jsoncContent, valueType);
    }
}
```

### All-in-One JAR使用時（intra-mart向け）
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

## 環境別推奨事項

### モダンなSpring Boot環境
- **推奨**: Slim JAR
- **理由**: Spring Bootが最新のJacksonを管理するため

### 従来のWebアプリケーションサーバー
- **推奨**: All-in-One JAR
- **理由**: サーバー側のJacksonとの競合を回避

### intra-mart環境
- **推奨**: All-in-One JAR
- **理由**: intra-martが特定のJacksonバージョンを固定しているため

### マイクロサービス環境
- **推奨**: Slim JAR
- **理由**: コンテナサイズの最適化とDependency Injectionの活用

## トラブルシューティング

### 依存関係競合エラーが発生した場合
1. All-in-One JARに切り替える
2. または、除外設定を使用してJacksonの競合を解決

### ClassNotFoundExceptionが発生した場合
1. Slim JAR使用時: Jackson 2.18.4の依存関係を確認
2. All-in-One JAR使用時: JARファイルがクラスパスに含まれているか確認

### パフォーマンスの考慮
- Slim JAR: 起動時間の最適化
- All-in-One JAR: メモリ使用量がわずかに増加する可能性
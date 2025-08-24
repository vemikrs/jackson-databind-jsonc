言語: 日本語 | [English](./README_en.md)

# Jackson-Databind-Jsonc

このプロジェクトは、Jackson の `JsonMapper` を拡張し、JSONC（コメント付きJSON）を扱える `JsoncMapper` を提供します。

## 特長

- JSONC 形式をサポート（ブロックコメント `/* */` と 行末コメント `//`）
- Jackson の `JsonMapper` を拡張
- 複数の Java バージョンをサポート（Java 8, 11, 17, 21, 24）
- 利用シーンに応じた 2 つの配布形式を提供

## 要件

### Java バージョン対応

本ライブラリは次の Java バージョンをサポートします。

- Java 8（LTS）: 最低要件
- Java 11（LTS）: フル対応
- Java 17（LTS）: フル対応
- Java 21（LTS）: フル対応
- Java 24: 利用可能（リリース後）

最大互換性のため、バイトコードは Java 8（major version 52）でコンパイルされ、すべてのサポート対象 Java でビルド・テストされます。

### テスト方針

- ビルドテスト: Gradle（Java 17+が必要）でビルド・テスト
- 実行時互換性: 生成された JAR は Java 8+ で動作確認
- バイトコード: すべてのクラスを Java 8 バイトコードでコンパイル

## 配布形式

さまざまな環境要件に対応するため、2 つの配布形式を提供します。

### Slim JAR（推奨）
- ファイル: `jackson-databind-jsonc-1.0.0.jar`（約 5KB）
- 用途: モダンな Jackson 環境、Maven/Gradle プロジェクト
- 依存関係: Jackson 2.18.4+ が必要
- 特徴: 軽量で依存関係管理が柔軟

### All-in-One JAR（エンタープライズ）
- ファイル: `jackson-databind-jsonc-1.0.0-all.jar`（約 7.8MB）
- 用途: エンタープライズ Java アプリ、レガシー環境、依存関係競合回避
- 依存関係: 自己完結（Jackson 同梱）
- 特徴: 単一ファイル配布、依存関係競合なし

## インストール

### Maven（Slim JAR）
```xml
<dependency>
    <groupId>jp.vemi</groupId>
    <artifactId>jackson-databind-jsonc</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle（Slim JAR）
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.0'
```

### 手動インストール
1. [Releases ページ](https://github.com/vemic/jackson-databind-jsonc/releases) から JAR をダウンロード
2. プロジェクトのクラスパスに追加

## どちらの JAR を使うべきか

### Slim JAR を選ぶ場合
- ✅ Jackson 2.18.x+ を利用可能
- ✅ 依存関係を Maven/Gradle で管理できる
- ✅ 他ライブラリとの競合がない
- ✅ JAR サイズを最小化したい

### All-in-One JAR を選ぶ場合
- ✅ エンタープライズ Java アプリケーション環境
- ✅ 既存の Jackson バージョンが固定されている
- ✅ 依存関係の競合を避けたい
- ✅ 単一 JAR 配布が必要
- ✅ 複雑な依存関係管理を避けたい

## 使い方

### 基本的な使用方法（両 JAR 共通）
```java
import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Example {
    public static void main(String[] args) {
        // JSONC はブロックコメントと行末コメントの両方をサポート
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

### Spring Boot 環境（Slim JAR）
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

### エンタープライズ Java アプリケーション（All-in-One JAR）
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

## 環境別の推奨

### モダンな Spring Boot
- 推奨: Slim JAR
- 理由: Spring Boot が最新の Jackson を管理

### 従来型 Web アプリケーションサーバ
- 推奨: All-in-One JAR
- 理由: サーバ側 Jackson との競合を回避

### エンタープライズ Java 環境
- 推奨: All-in-One JAR
- 理由: 固定バージョンの Jackson を利用するケースが多い

### マイクロサービス
- 推奨: Slim JAR
- 理由: コンテナサイズ最適化と依存性注入

## 開発

### ビルドとテスト
本プロジェクトは Gradle を使用します。ビルドには Java 21 が必要です。

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

### GitHub ワークフロー

#### PR テスト実行
手動または PR イベントで実行できるテスト実行ワークフローを含みます。

【手動実行】
1. GitHub の Actions タブへ
2. "PR Test Execution" ワークフローを選択
3. "Run workflow" をクリック
4. オプションを設定:
   - Test Scope: `all`, `unit-only`, `security-only`, `integration-only`
   - Run Build: テスト前にビルドを実行するか
   - PR Number: テスト対象の PR を指定可能（任意）

【自動実行】
PR の opened/synchronize/ready_for_review で自動的に実行（フルスコープ）

【特徴】
- スコープ別の選択実行
- 詳細なテスト結果とサマリー
- PR への結果コメント
- Gradle のキャッシュによる高速化
- タイムアウト保護（最大 10 分）

## トラブルシューティング

### 依存関係の競合
1. All-in-One JAR に切替
2. もしくは除外設定で Jackson の競合を解消

### ClassNotFoundException
1. Slim JAR: Jackson 2.18.4 依存を確認
2. All-in-One JAR: JAR が classpath にあるか確認

### パフォーマンスの考慮事項
- Slim JAR: 起動時間に有利
- All-in-One JAR: メモリ使用量がわずかに増える場合あり

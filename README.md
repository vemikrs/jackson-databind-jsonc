言語: 日本語 | [English](./README_en.md)

# Jackson-Databind-Jsonc

[![Maven Central](https://img.shields.io/maven-central/v/jp.vemi/jackson-databind-jsonc.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/jp.vemi/jackson-databind-jsonc)
[![javadoc](https://javadoc.io/badge2/jp.vemi/jackson-databind-jsonc/latest/javadoc.svg)](https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/latest/)
[![Release Workflow](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml/badge.svg)](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml)

このプロジェクトは、Jackson の `JsonMapper` を拡張し、JSONC（コメント付きJSON）を扱える `JsoncMapper` を提供します。

## 特長

- JSONC 形式をサポート（ブロックコメント `/* */` と 行末コメント `//`）
- Builder パターンによるオプションのJSON5機能
  - シングルクォート文字列（`'text'` → `"text"`）
  - 16進数リテラル（`0xFF` → `255`）
  - プラス記号付き数値（`+123` → `123`）
  - 無限大やNaN（`Infinity`/`NaN` → `null`）
  - 複数行文字列とエスケープされていない制御文字
- Jackson の `JsonMapper` を拡張
- 複数の Java バージョンをサポート（Java 8, 11, 17, 21, 24）
- 2 つの配布形式（Slim / All-in-One）
- ReDoS攻撃に対する保護機能（線形時間アルゴリズム）
- トレーリングカンマ除去機能（オプション）

## サポートされるコメント形式

### ✅ 完全サポート
- ブロックコメント: `/* コメント */`（複数行）
- 行末コメント: `// コメント`（行末まで）
- マルチラインコメント（複数行）
- 文字列内コメント保護（JSON文字列内のコメントは保持）

```javascript
{
  /* 設定ファイルのメインセクション */
  "database": {
    "host": "localhost", // デフォルトホスト
    "port": 5432,
    /* 複数行コメント
       データベース設定の
       詳細説明 */
    "timeout": 30
  },
  "message": "This /* is not removed */ from string" // 文字列内は保護
}
```

### ❌ 未対応機能
- ネストコメント: `/* 外側 /* 内側 */ 外側 */`
- JSON5のその他機能（オブジェクトキーの引用符省略 等）

### 🔧 オプションのJSON5機能（Builderで選択）

- トレーリングカンマ除去
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowTrailingCommas(true)
    .build();
```

- シングルクォート文字列
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowSingleQuotes(true)
    .build();
// 入力: { 'key': 'value' }
// 出力: { "key": "value" }
```

- 16進数リテラル
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowHexNumbers(true)
    .build();
// 入力: { "value": 0xFF }
// 出力: { "value": 255 }
```

- プラス記号付き数値
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowPlusNumbers(true)
    .build();
// 入力: { "value": +123 }
// 出力: { "value": 123 }
```

- 無限大とNaN
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowInfinityAndNaN(true)
    .build();
// 入力: { "inf": Infinity, "nan": NaN }
// 出力: { "inf": null, "nan": null }
```

- 複数行/制御文字
```java
JsoncMapper mapper = new JsoncMapper.Builder()
    .allowMultilineStrings(true)
    .allowUnescapedControlChars(true)
    .build();
```

- すべての機能を組み合わせ
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

## 要件

- Java 8（最低要件）/ 11 / 17 / 21 / 24（予定）
- ビルド: Java 21 が必要（生成バイトコードは `--release 8` により Java 8 互換）

## 配布形式

### Slim JAR（推奨）
- ファイル: `jackson-databind-jsonc-<version>.jar`（~5KB）
- 用途: モダンな Jackson 環境、Maven/Gradle プロジェクト
- 依存関係: Jackson Databind 2.20.0+ が必要
- Maven: `jp.vemi:jackson-databind-jsonc:<version>`
- Gradle: `implementation("jp.vemi:jackson-databind-jsonc:<version>")`

### All-in-One JAR（エンタープライズ）
- ファイル: `jackson-databind-jsonc-<version>-all.jar`（~7.8MB）
- 用途: エンタープライズ/レガシー環境、依存関係競合回避
- 依存関係: 自己完結（Jackson 同梱）
- Maven: `jp.vemi:jackson-databind-jsonc-all:<version>`
- Gradle: `implementation("jp.vemi:jackson-databind-jsonc-all:<version>")`

## インストール

> 最新版は Maven Central から取得可能（自動公開: Central Portal）

### Maven（Slim JAR）
```xml
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc</artifactId>
  <version>1.0.5</version>
  </dependency>
```

### Gradle（Slim JAR）
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:1.0.5'
```

### Maven（All-in-One JAR）
```xml
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc-all</artifactId>
  <version>1.0.5</version>
</dependency>
```

### Gradle（All-in-One JAR）
```groovy
implementation 'jp.vemi:jackson-databind-jsonc-all:1.0.5'
```

### 手動インストール
1. [Releases](https://github.com/vemikrs/jackson-databind-jsonc/releases) から JAR をダウンロード
2. プロジェクトのクラスパスに追加

**📋 リリース情報:**
- 自動公開: Maven Central Portal 経由
- 手動取得: GitHub Releases でJARにアクセス
- 詳細: [PUBLISHING.md](./PUBLISHING.md)

## API ドキュメント

- 最新版（javadoc.io）: https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/latest/
- バージョン指定例: https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/1.0.5/
- ローカル生成: `./gradlew javadoc` 後 `lib/build/docs/javadoc/index.html`

## どちらの JAR を使うべきか

### Slim JAR を選ぶ場合
- ✅ Jackson 2.20.0+ を利用可能
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

## パフォーマンス

- 通常サイズ（< 10MB）: 高速、メモリ使用量は約1.5倍
- 大きなファイル（> 10MB）: 事前コメント除去を推奨

## セキュリティ

- 正規表現非依存の線形時間アルゴリズムで ReDoS 耐性
- null 入力は `IllegalArgumentException` を送出
- 文字列内のコメント風テキストは保持

## 開発

### ビルドとテスト
- ビルドには Java 21 が必要（生成バイトコードは `--release 8` で Java 8 互換）

```bash
./gradlew build
./gradlew test
```

### CI / Build & Test Status
- Release Workflow: 上部バッジ、または [Actions > Release](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml)
- テストレポート（ローカル）: `lib/build/reports/tests/test/index.html`
- GitHub Actions での一般的な確認手順:
  - Actions タブ → 対象ワークフロー → 実行ログの Summary で成功/失敗を確認
  - 必要に応じてアップロード済みアーティファクト（JAR等）をダウンロード

## トラブルシューティング（抜粋）

### 依存関係の競合
```xml
<!-- Maven の場合（All-in-One は別アーティファクト） -->
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc-all</artifactId>
  <version>1.0.5</version>
</dependency>
```

### ClassNotFoundException
- Slim JAR: Jackson 2.20.0+ 依存を確認
- All-in-One JAR: 単一JARが classpath にあるか確認

### ビルド関連
```bash
java -version # Java 21 を確認
export JAVA_HOME=/path/to/java21
./gradlew clean build --refresh-dependencies
```

## リソース
- [MIGRATION_NOTES.md](./MIGRATION_NOTES.md)
- [PUBLISHING.md](./PUBLISHING.md)
- [SECURITY.md](./SECURITY.md)
- [LICENSE](./LICENSE)

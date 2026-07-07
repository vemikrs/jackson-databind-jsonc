言語: 日本語 | [English](./README_en.md)

# Jackson-Databind-Jsonc

[![Maven Central](https://img.shields.io/maven-central/v/jp.vemi/jackson-databind-jsonc.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/jp.vemi/jackson-databind-jsonc)
[![javadoc](https://javadoc.io/badge2/jp.vemi/jackson-databind-jsonc/latest/javadoc.svg)](https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/latest/)
[![Release Workflow](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml/badge.svg)](https://github.com/vemikrs/jackson-databind-jsonc/actions/workflows/release.yml)

このプロジェクトは、Jackson の `JsonMapper` を拡張し、JSONC（コメント付きJSON）を扱える `JsoncMapper` を提供します。

> **バージョンライン**: `1.x` は Jackson 2 系（Java 8+）向けの現行ラインです。`2.x` は Jackson 3 系（`tools.jackson`、Java 17+）向けの純プリセット実装として別途提供予定です。利用中の Jackson メジャーに合わせてラインを選択してください。

## 特長

- JSONC 形式をサポート（ブロックコメント `/* */` と 行末コメント `//`）
- Builder パターンによるオプションのJSON5機能
  - シングルクォート文字列（`'text'` → `"text"`）
  - 16進数リテラル（`0xFF` → `255`）
  - プラス記号付き数値（`+123` → `123`）
  - 無限大やNaN（`Infinity`/`NaN` を数値として読み込み。Jackson標準の `ALLOW_NON_NUMERIC_NUMBERS` に委譲）
  - 複数行文字列とエスケープされていない制御文字
- Jackson の `JsonMapper` を拡張
- 複数の Java バージョンをサポート（Java 8, 11, 17, 21, 24）
- 2 つの配布形式（Slim / All-in-One）
- ReDoS攻撃に対する保護機能（線形時間アルゴリズム）
- トレーリングカンマ除去機能（オプション）

## Jackson 標準機能との使い分け

単純な JSONC（コメント・トレーリングカンマ・シングルクォート）を読むだけであれば、追加ライブラリを入れなくても Jackson 本体の [`JsonReadFeature`](https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-core/latest/com/fasterxml/jackson/core/json/JsonReadFeature.html)（2.10+）で対応できます。

```java
// Jackson 標準機能だけで足りるケース
ObjectMapper mapper = JsonMapper.builder()
    .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
    .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
    .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
    .build();
```

**本ライブラリを採用する価値があるのは、次のいずれかに当てはまる場合です。**

- 既存の `ObjectMapper` 系コードを `new JsoncMapper()` に差し替えるだけで JSONC を読ませたい
- 16進数リテラル（`0xFF`）、`+123`、`Infinity`/`NaN` の吸収など、JSON5 風の前処理をまとめて1か所で持ちたい
- 有効化したい機能を Builder のフラグで宣言的に管理したい

**逆に、次の場合は Jackson 標準機能で十分で、本ライブラリは不要です。**

- コメント／トレーリングカンマ／シングルクォートしか使わない
- 大容量（数十MB〜）や真のストリーミング処理が必要（本ライブラリは入力を全文文字列化してから前処理する方式のため不向き）

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
// 読み込み結果: inf=Double.POSITIVE_INFINITY, nan=Double.NaN（数値として保持）
```
> ℹ️ このオプションは Jackson 標準の `JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS` に委譲し、`Infinity`/`-Infinity`/`NaN` を実際の浮動小数点値として読み込みます。受け側が `double`/`Double`（または `Object`）であれば値が保持されます。
> なお、旧バージョンではこれらを前処理で `null` に置換していましたが、値が黙って欠落する挙動だったため廃止しました。

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
- 依存関係: `jackson-databind` を `api` スコープで推移的に提供（ビルド時は 2.22.0、実行時は 2.20.0 以上と互換）。Maven/Gradle が自動解決するため、利用側で個別追加は不要
- Maven: `jp.vemi:jackson-databind-jsonc:<version>`
- Gradle: `implementation("jp.vemi:jackson-databind-jsonc:<version>")`

### All-in-One JAR（エンタープライズ）
- ファイル: `jackson-databind-jsonc-<version>-all.jar`（~7.8MB）
- 用途: エンタープライズ/レガシー環境、依存関係競合回避
- 依存関係: 自己完結（Jackson 同梱）
- Maven: `jp.vemi:jackson-databind-jsonc-all:<version>`
- Gradle: `implementation("jp.vemi:jackson-databind-jsonc-all:<version>")`

## インストール

> 下記の `LATEST_VERSION` は、上部の Maven Central バッジが示す最新版に置き換えてください。版指定済みのコピペ用スニペットは各 [Releases](https://github.com/vemikrs/jackson-databind-jsonc/releases) にも掲載されています。

### Maven（Slim JAR）
```xml
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc</artifactId>
  <version>LATEST_VERSION</version>
  </dependency>
```

### Gradle（Slim JAR）
```groovy
implementation 'jp.vemi:jackson-databind-jsonc:LATEST_VERSION'
```

### Maven（All-in-One JAR）
```xml
<dependency>
  <groupId>jp.vemi</groupId>
  <artifactId>jackson-databind-jsonc-all</artifactId>
  <version>LATEST_VERSION</version>
</dependency>
```

### Gradle（All-in-One JAR）
```groovy
implementation 'jp.vemi:jackson-databind-jsonc-all:LATEST_VERSION'
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
- バージョン指定例: `https://javadoc.io/doc/jp.vemi/jackson-databind-jsonc/{version}/`（`{version}` を実際の版に置換）
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
  <version>LATEST_VERSION</version>
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

# Jackson-Databind-Jsonc

このプロジェクトは、Jacksonの `JsonMapper` を拡張し、JSONC（コメント付きJSON）形式を処理するための新しい **`JsoncMapper`** を追加します。

## 特徴

- ブロックコメント（`/* */`）および行末コメント（`//`）をサポートするJSONC形式
- Jacksonの `JsonMapper` を拡張
- マルチバージョンJavaサポート（Java 8, 11, 17, 21, 24）

## 要件

### Javaバージョンサポート

このライブラリは複数のJavaバージョンをサポートしています：

- **Java 8** (LTS) - 最小必要バージョン
- **Java 11** (LTS) - 完全サポート
- **Java 17** (LTS) - 完全サポート
- **Java 21** (LTS) - 完全サポート
- **Java 24** - サポート（利用可能な場合）

ライブラリは最大限の互換性のためにJava 8バイトコードにコンパイルされ、サポートされているすべてのJavaバージョンでビルドおよびテストされています。

### テストアプローチ

- **ビルドテスト**: プロジェクトはJava 17+でビルドおよびテストされます（Gradleによる要求）
- **ランタイム互換性**: 生成されたJARはJava 8+での互換性がテストされます
- **バイトコードターゲット**: すべてのクラスはJava 8バイトコード（メジャーバージョン52）でコンパイルされます

## インストール

### 依存関係を追加

jackson-databindをインストールする必要があります。  
バージョンは任意です。

#### Mavenを使用する場合
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

#### Gradleを使用する場合
```groovy
implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.0'
```

### jarファイルの追加

1. リリースページから最新のjarファイルをダウンロードします。
1. プロジェクトのクラスパスにjarファイルを追加します。

## 使用方法

`JsoncMapper`を使用するサンプルコードは以下の通りです。

```java
import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Example {
    public static void main(String[] args) {
        // JSONCはブロックコメントと行末コメントの両方をサポートします
        String jsonWithComments = """
            {
                /* ブロックコメント */
                "name": "example", // 行末コメント
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

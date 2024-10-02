# Jackson-Databind-Jsonc

このプロジェクトは、Jacksonの `JsonMapper` を拡張し、JSONC（コメント付きJSON）形式を処理するための新しい **`JsoncMapper`** を追加します。

## 特徴

- JSONC形式をサポート
- Jacksonの `JsonMapper` を拡張

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
        String jsonWithComments = "/* これはコメントです */ { \"key\": \"value\" }";
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

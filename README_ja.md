# Jackson-Databind-Jsonc

このプロジェクトはJacksonの`JsonMapper`を拡張し、JSONC（コメント付きJSON）フォーマットを処理するための`JsoncMapper`を追加します。

## 特徴

- Jacksonの`JsonMapper`を拡張
- JSONCフォーマットをサポート

## 使用法

`JsoncMapper`を使用するには:

```java
import com.fasterxml.jackson.databind.json.JsoncMapper;

public class Example {
    public static void main(String[] args) {
        JsoncMapper mapper = new JsoncMapper();
        // ここにコードを追加
    }
}

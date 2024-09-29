package jp.vemi.jsoncmapper;

public class JsoncUtils {
    public static String removeComments(String jsonc) {
        return jsonc.replaceAll("(?s)//.*?\n|/\\*.*?\\*/", "");
    }
}

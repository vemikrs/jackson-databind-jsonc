import jp.vemi.jsoncmapper.JsoncUtils;

class TestIssues {
    public static void main(String[] args) {
        // Test Issue 1: isPlusBeforeNumber - scientific notation
        System.out.println("=== Issue 1: Scientific notation ===");
        String json1 = "{ \"value\": +1e5, \"value2\": +1.5E-3, \"value3\": +.5e10 }";
        String result1 = JsoncUtils.removePlusFromNumbers(json1);
        System.out.println("Input:  " + json1);
        System.out.println("Output: " + result1);
        
        // Test edge cases that might fail
        String json1b = "{ \"test\": +1e, \"invalid\": +e5 }"; // These should NOT be treated as numbers
        String result1b = JsoncUtils.removePlusFromNumbers(json1b);
        System.out.println("Edge case input:  " + json1b);
        System.out.println("Edge case output: " + result1b);
        System.out.println("(+ should only be removed if followed by valid number)");
        
        // Test Issue 2: multiline strings - should only affect strings, not structure
        System.out.println("=== Issue 2: Multiline strings ===");
        String json2 = "{\n  \"key\": \"value\",\n  \"other\": \"test\"\n}";
        String result2 = JsoncUtils.convertMultilineStrings(json2);
        System.out.println("Input:");
        System.out.println(json2);
        System.out.println("Output:");
        System.out.println(result2);
        System.out.println("Problem: All newlines are replaced, corrupting JSON structure");
        System.out.println();
        
        // Test Issue 3: control chars - already escaped should not be re-escaped
        System.out.println("=== Issue 3: Control character escaping ===");
        String json3 = "{ \"key\": \"\\u0001already escaped\", \"other\": \"unescaped\u0001char\" }";
        String result3 = JsoncUtils.escapeControlChars(json3);
        System.out.println("Input:  " + json3);
        System.out.println("Output: " + result3);
        System.out.println("Problem: The logic may not properly handle already escaped sequences");
    }
}
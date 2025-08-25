import jp.vemi.jsoncmapper.JsoncUtils;

class TestUnicodeEdgeCase {
    public static void main(String[] args) {
        System.out.println("=== Testing Unicode Escape Edge Case ===");
        
        // The potential problem: If we have a string like "\\u000" followed by a control char
        // The escaped flag would be false by the time we reach the control char
        String problematic = "{ \"key\": \"\\\\u000\u0001test\" }";
        String result = JsoncUtils.escapeControlChars(problematic);
        
        System.out.println("Input:  " + problematic);
        System.out.println("Output: " + result);
        System.out.println();
        System.out.println("Analysis:");
        System.out.println("- \\\\u000 is not a complete unicode sequence (missing last digit)");
        System.out.println("- The \\u0001 after it should be properly escaped");
        System.out.println("- Current logic might not handle this correctly");
        System.out.println();
        
        // Another edge case
        String test2 = "{ \"key\": \"\\\\u00\u0001incomplete\" }";
        String result2 = JsoncUtils.escapeControlChars(test2);
        System.out.println("Test 2:");
        System.out.println("Input:  " + test2);
        System.out.println("Output: " + result2);
        
        // Test what happens with backslash followed by other chars then control char
        String test3 = "{ \"key\": \"\\\\x\u0001test\" }";
        String result3 = JsoncUtils.escapeControlChars(test3);
        System.out.println("Test 3 (\\\\x followed by control char):");
        System.out.println("Input:  " + test3);
        System.out.println("Output: " + result3);
    }
}
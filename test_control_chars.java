import jp.vemi.jsoncmapper.JsoncUtils;

class TestControlChars {
    public static void main(String[] args) {
        System.out.println("=== Testing Control Character Escaping ===");
        
        // Test 1: Already escaped unicode sequence
        String test1 = "{ \"key\": \"\\u0001already escaped\" }";
        String result1 = JsoncUtils.escapeControlChars(test1);
        System.out.println("Test 1 - Already escaped:");
        System.out.println("Input:  " + test1);
        System.out.println("Output: " + result1);
        System.out.println("Should stay unchanged");
        System.out.println();
        
        // Test 2: Unescaped control character
        String test2 = "{ \"key\": \"unescaped\u0001char\" }";
        String result2 = JsoncUtils.escapeControlChars(test2);
        System.out.println("Test 2 - Unescaped control char:");
        System.out.println("Input:  " + test2);
        System.out.println("Output: " + result2);
        System.out.println("Should escape the control char");
        System.out.println();
        
        // Test 3: Mixed case - some escaped, some not
        String test3 = "{ \"key\": \"\\u0001escaped and \u0002unescaped\" }";
        String result3 = JsoncUtils.escapeControlChars(test3);
        System.out.println("Test 3 - Mixed:");
        System.out.println("Input:  " + test3);
        System.out.println("Output: " + result3);
        System.out.println();
        
        // Test 4: Test the problematic case mentioned in review
        String test4 = "{ \"key\": \"\\u0001test\u0003more\" }";
        String result4 = JsoncUtils.escapeControlChars(test4);
        System.out.println("Test 4 - Potential problem case:");
        System.out.println("Input:  " + test4);
        System.out.println("Output: " + result4);
        System.out.println("The first \\u0001 should stay, \\u0003 should be escaped");
    }
}
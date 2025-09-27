package jp.vemi.jsoncmapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

public class SecurityTest {
    
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testReDoSProtection() {
        // This test verifies protection against ReDoS attacks
        // The old regex pattern could cause catastrophic backtracking with this input
        StringBuilder maliciousInput = new StringBuilder("/*");
        // Create a string that could cause catastrophic backtracking in vulnerable regex
        for (int i = 0; i < 1000; i++) {
            maliciousInput.append("/*");
        }
        maliciousInput.append("*/");
        
        // This should complete quickly, not hang
        String result = JsoncUtils.removeComments(maliciousInput.toString());
        assertNotNull(result);
    }
    
    @Test
    public void testMalformedCommentHandling() {
        // Test edge cases that could cause issues
        String[] testCases = {
            "/* unclosed comment",
            "/* nested /* comment */ */",
            "// line comment without newline",
            "/**//**//**/",
            "/* comment with \r\n newlines */",
            "\"string with /* fake comment */\"",
            "/* comment */ /* another */ /* third */"
        };
        
        for (String testCase : testCases) {
            assertDoesNotThrow(() -> {
                String result = JsoncUtils.removeComments(testCase);
                assertNotNull(result);
            }, "Should handle edge case: " + testCase);
        }
    }
    
    @Test
    public void testStringProtection() {
        // Ensure comments inside strings are not removed
        String jsonWithStringComment = "{ \"value\": \"/* this is not a comment */\" }";
        String result = JsoncUtils.removeComments(jsonWithStringComment);
        assertTrue(result.contains("/* this is not a comment */"), 
                  "Comments inside strings should not be removed");
    }

    // Additional Security Tests
    @Test
    public void testMaliciousNestedComments() {
        // Test with deeply nested comment-like patterns
        String maliciousInput = "/* /* /* /* /* */ */ */ */ */";
        assertDoesNotThrow(() -> {
            String result = JsoncUtils.removeComments(maliciousInput);
            assertNotNull(result);
        });
    }

    @Test
    public void testStringWithEscapedQuotes() {
        // Test that escaped quotes don't break comment parsing
        String jsonc = "{ \"escaped\": \"He said \\\"/* not a comment */\\\" today\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertTrue(result.contains("/* not a comment */"), 
                  "Comments inside strings with escaped quotes should not be removed");
    }

    @Test
    public void testStringWithBackslashes() {
        // Test handling of backslashes in strings
        String jsonc = "{ \"path\": \"C:\\\\Users\\\\/* not a comment */\\\\Documents\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertTrue(result.contains("/* not a comment */"), 
                  "Comments inside strings with backslashes should not be removed");
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testVeryLongString() {
        // Test performance with very long strings containing comment-like patterns
        StringBuilder longString = new StringBuilder("{ \"data\": \"");
        for (int i = 0; i < 10000; i++) {
            longString.append("/* fake comment ").append(i).append(" */ ");
        }
        longString.append("\" }");
        
        String result = JsoncUtils.removeComments(longString.toString());
        assertNotNull(result);
        assertTrue(result.contains("/* fake comment"), 
                  "Comment patterns inside long strings should be preserved");
    }

    @Test
    public void testMixedQuoteTypes() {
        // Test with mixed quote scenarios that could confuse parsers
        String jsonc = "{ /* comment */ \"key\": \"value with ' single quote\" }";
        String expected = "{  \"key\": \"value with ' single quote\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentBoundaryEdgeCases() {
        // Test edge cases around comment boundaries
        String[] edgeCases = {
            "/*/",           // Minimal comment
            "/**/",          // Empty comment
            "/*/*/",         // Comment starting with comment start
            "/**/*",         // Comment ending with comment start
            "/*****/",       // Comment with extra stars
        };
        
        for (String testCase : edgeCases) {
            assertDoesNotThrow(() -> {
                String result = JsoncUtils.removeComments(testCase);
                assertNotNull(result);
            }, "Should handle edge case: " + testCase);
        }
    }

    // Security Tests for Trailing Comma Functionality
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testTrailingCommaReDoSProtection() {
        // Test protection against ReDoS attacks in trailing comma detection
        StringBuilder maliciousInput = new StringBuilder("{ \"key\": \"value\"");
        
        // Create a string with many commas that could cause catastrophic backtracking
        for (int i = 0; i < 1000; i++) {
            maliciousInput.append(",,,");
        }
        maliciousInput.append(" }");
        
        // This should complete quickly, not hang
        String result = JsoncUtils.removeTrailingCommas(maliciousInput.toString());
        assertNotNull(result);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testCombinedReDoSProtection() {
        // Test protection against ReDoS attacks in combined processing
        StringBuilder maliciousInput = new StringBuilder("/*");
        
        // Create a pathological case with many nested patterns
        for (int i = 0; i < 500; i++) {
            maliciousInput.append("/* comment ").append(i).append(" */,");
        }
        maliciousInput.append("*/ { \"key\": \"value\", }");
        
        // This should complete quickly, not hang
        String result = JsoncUtils.removeCommentsAndTrailingCommas(maliciousInput.toString());
        assertNotNull(result);
    }

    @Test
    public void testTrailingCommaStringProtection() {
        // Ensure commas inside strings are never treated as trailing commas
        String jsonc = "{ \"malicious\": \"} ],\" , }";
        String expected = "{ \"malicious\": \"} ],\"  }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testTrailingCommaWithMaliciousStrings() {
        // Test with strings that contain JSON-like structure
        String jsonc = "{ \"fake_json\": \"{ \\\"inner\\\": \\\"value\\\", }\", }";
        String expected = "{ \"fake_json\": \"{ \\\"inner\\\": \\\"value\\\", }\" }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testTrailingCommaVeryLongString() {
        // Test performance with very long strings containing commas
        StringBuilder longString = new StringBuilder("{ \"data\": \"");
        for (int i = 0; i < 10000; i++) {
            longString.append("text,with,commas,");
        }
        longString.append("\", }");
        
        String result = JsoncUtils.removeTrailingCommas(longString.toString());
        assertNotNull(result);
        assertTrue(result.endsWith("\" }"));
    }

    @Test
    public void testTrailingCommaDeepNesting() {
        // Test with deeply nested structures to ensure O(n) performance
        StringBuilder deeplyNested = new StringBuilder();
        int depth = 100;
        
        // Create deep nesting
        for (int i = 0; i < depth; i++) {
            deeplyNested.append("{ \"level").append(i).append("\": ");
        }
        deeplyNested.append("\"value\"");
        for (int i = 0; i < depth; i++) {
            deeplyNested.append(", }");
        }
        
        String result = JsoncUtils.removeTrailingCommas(deeplyNested.toString());
        assertNotNull(result);
        assertFalse(result.contains(", }"));
    }

    // Security Tests for JSON5 Features
    
    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testSingleQuoteReDoSProtection() {
        // Test with many nested single quotes that could cause performance issues
        StringBuilder maliciousInput = new StringBuilder("{ ");
        for (int i = 0; i < 1000; i++) {
            maliciousInput.append("'key").append(i).append("': 'value").append(i).append("', ");
        }
        maliciousInput.append("'final': 'value' }");
        
        String result = JsoncUtils.convertSingleQuotes(maliciousInput.toString());
        assertNotNull(result);
        assertTrue(result.contains("\"final\": \"value\""));
    }
    
    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testHexNumberReDoSProtection() {
        // Test with many hex numbers that could cause performance issues
        StringBuilder maliciousInput = new StringBuilder("{ ");
        for (int i = 0; i < 1000; i++) {
            maliciousInput.append("\"key").append(i).append("\": 0x").append(Integer.toHexString(i)).append(", ");
        }
        maliciousInput.append("\"final\": 0xFF }");
        
        String result = JsoncUtils.convertHexNumbers(maliciousInput.toString());
        assertNotNull(result);
        assertTrue(result.contains("\"final\": 255"));
    }
    
    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testPlusNumberReDoSProtection() {
        // Test with many plus numbers that could cause performance issues
        StringBuilder maliciousInput = new StringBuilder("{ ");
        for (int i = 0; i < 1000; i++) {
            maliciousInput.append("\"key").append(i).append("\": +").append(i).append(", ");
        }
        maliciousInput.append("\"final\": +999 }");
        
        String result = JsoncUtils.removePlusFromNumbers(maliciousInput.toString());
        assertNotNull(result);
        assertTrue(result.contains("\"final\": 999"));
    }
    
    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testInfinityNaNReDoSProtection() {
        // Test with many Infinity/NaN values
        StringBuilder maliciousInput = new StringBuilder("{ ");
        for (int i = 0; i < 500; i++) {
            maliciousInput.append("\"inf").append(i).append("\": Infinity, ");
            maliciousInput.append("\"nan").append(i).append("\": NaN, ");
        }
        maliciousInput.append("\"final\": Infinity }");
        
        String result = JsoncUtils.convertInfinityAndNaN(maliciousInput.toString());
        assertNotNull(result);
        assertTrue(result.contains("\"final\": null"));
    }
    
    @Test
    public void testJson5StringProtection() {
        // Test that JSON5 features don't affect strings containing similar patterns
        String json5 = "{ \"message\": \"Don't convert 'single quotes' or 0xFF or +123 or Infinity in strings\", " +
                      "'actualSingle': 'value', \"actualHex\": 0xFF, \"actualPlus\": +123, \"actualInf\": Infinity }";
        
        String step1 = JsoncUtils.convertSingleQuotes(json5);
        String step2 = JsoncUtils.convertHexNumbers(step1);
        String step3 = JsoncUtils.removePlusFromNumbers(step2);
        String step4 = JsoncUtils.convertInfinityAndNaN(step3);
        
        // String content should be preserved
        assertTrue(step4.contains("'single quotes'"));
        assertTrue(step4.contains("or 0xFF or"));
        assertTrue(step4.contains("or +123 or"));
        assertTrue(step4.contains("or Infinity in"));
        
        // But actual JSON5 features should be converted
        assertTrue(step4.contains("\"actualSingle\": \"value\""));
        assertTrue(step4.contains("\"actualHex\": 255"));
        assertTrue(step4.contains("\"actualPlus\": 123"));
        assertTrue(step4.contains("\"actualInf\": null"));
    }
    
    @Test
    public void testJson5MalformedInput() {
        // Test with malformed JSON5 that could cause issues
        String malformed1 = "{ 'unclosed string }";
        String result1 = JsoncUtils.convertSingleQuotes(malformed1);
        assertNotNull(result1);
        
        String malformed2 = "{ \"value\": 0x }";  // Invalid hex
        String result2 = JsoncUtils.convertHexNumbers(malformed2);
        assertNotNull(result2);
        assertEquals(malformed2, result2); // Should remain unchanged
        
        String malformed3 = "{ \"value\": + }";  // Invalid plus number
        String result3 = JsoncUtils.removePlusFromNumbers(malformed3);
        assertNotNull(result3);
        assertEquals(malformed3, result3); // Should remain unchanged
    }
    
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testJson5CombinedWithLargeInput() {
        // Test JSON5 features with large input similar to other security tests
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeInput.append("'key").append(i).append("': 'value with 0xFF and +123',");
        }
        largeInput.append("'final': 'done'");
        
        String json5 = "{ " + largeInput.toString() + " }";
        
        // Apply all transformations
        String result = JsoncUtils.convertSingleQuotes(json5);
        result = JsoncUtils.convertHexNumbers(result);
        result = JsoncUtils.removePlusFromNumbers(result);
        
        assertNotNull(result);
        assertTrue(result.contains("\"final\": \"done\""));
    }
}
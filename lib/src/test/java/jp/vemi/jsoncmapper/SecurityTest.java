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
}
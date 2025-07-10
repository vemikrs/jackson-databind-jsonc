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
}
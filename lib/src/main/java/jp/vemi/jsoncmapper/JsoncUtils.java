package jp.vemi.jsoncmapper;

public class JsoncUtils {
    
    /**
     * Removes JSON comments while preserving strings and handling edge cases securely.
     * This implementation protects against ReDoS attacks and properly handles comments
     * inside strings.
     * 
     * Supported comment formats:
     * - Block comments: /* comment &#42;/
     * - End-of-line comments: &#47;&#47; comment
     * 
     * @param jsonc JSON with comments string
     * @return JSON string with comments removed
     * @throws IllegalArgumentException if input is null
     */
    public static String removeComments(String jsonc) {
        if (jsonc == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (jsonc.isEmpty()) {
            return jsonc;
        }
        
        StringBuilder result = new StringBuilder(jsonc.length());
        int length = jsonc.length();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = jsonc.charAt(i);
            char next = (i + 1 < length) ? jsonc.charAt(i + 1) : '\0';
            
            if (!inString) {
                // Check for comment start outside of strings
                if (current == '/' && next == '*') {
                    // Block comment - find the end safely
                    i = skipBlockComment(jsonc, i);
                    continue;
                } else if (current == '/' && next == '/') {
                    // Line comment - skip to end of line
                    i = skipLineComment(jsonc, i);
                    continue;
                } else if (current == '"') {
                    // Entering string
                    inString = true;
                    result.append(current);
                } else {
                    result.append(current);
                }
            } else {
                // Inside string
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    inString = false;
                }
                result.append(current);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Safely skips a block comment, handling unclosed comments.
     */
    private static int skipBlockComment(String jsonc, int startIndex) {
        int i = startIndex + 2; // Skip past /*
        int length = jsonc.length();
        
        while (i < length - 1) {
            if (jsonc.charAt(i) == '*' && jsonc.charAt(i + 1) == '/') {
                return i + 1; // Return index of '/' so outer loop will increment past it
            }
            i++;
        }
        
        // Unclosed comment - skip to end
        return length - 1;
    }
    
    /**
     * Safely skips a line comment.
     */
    private static int skipLineComment(String jsonc, int startIndex) {
        int i = startIndex + 2; // Skip past //
        int length = jsonc.length();
        
        while (i < length) {
            char c = jsonc.charAt(i);
            if (c == '\n' || c == '\r') {
                return i - 1; // Return index before newline so newline is preserved
            }
            i++;
        }
        
        // End of string reached
        return length - 1;
    }
}

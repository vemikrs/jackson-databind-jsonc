package jp.vemi.jsoncmapper;

/**
 * Utility class for JSONC comment removal and trailing comma processing.
 * 
 * <p>Provides secure, linear-time algorithms with ReDoS protection.
 * Supports block comments, line comments, and string content protection.
 * 
 * @since 1.0.0
 * @see JsoncMapper
 */
public class JsoncUtils {
    
    /**
     * Removes trailing commas from JSON while preserving strings and handling edge cases securely.
     * This implementation protects against ReDoS attacks and properly handles commas
     * inside strings and comments.
     * 
     * Trailing commas are removed from:
     * - JSON objects: { "key": "value", }
     * - JSON arrays: [ "item1", "item2", ]
     * 
     * @param jsonc JSON with potential trailing commas and comments
     * @return JSON string with trailing commas removed
     * @throws IllegalArgumentException if input is null
     */
    public static String removeTrailingCommas(String jsonc) {
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
        boolean inBlockComment = false;
        boolean inLineComment = false;
        
        for (int i = 0; i < length; i++) {
            char current = jsonc.charAt(i);
            char next = (i + 1 < length) ? jsonc.charAt(i + 1) : '\0';
            
            // Handle comments and strings
            if (!inBlockComment && !inLineComment) {
                if (!inString) {
                    if (current == '"') {
                        inString = true;
                        result.append(current);
                    } else if (current == '/' && next == '*') {
                        inBlockComment = true;
                        result.append(current);
                    } else if (current == '/' && next == '/') {
                        inLineComment = true;
                        result.append(current);
                    } else if (current == ',') {
                        // Check if this is a trailing comma by looking ahead
                        boolean isTrailing = isTrailingCommaWithComments(jsonc, i);
                        if (!isTrailing) {
                            result.append(current);
                        }
                        // If it's trailing, we skip it (don't append)
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
            } else if (inBlockComment) {
                if (current == '*' && next == '/') {
                    result.append(current);
                    result.append(next);
                    i++; // Skip the '/'
                    inBlockComment = false;
                    continue;
                }
                result.append(current);
            } else if (inLineComment) {
                if (current == '\n' || current == '\r') {
                    inLineComment = false;
                }
                result.append(current);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Look-ahead function that checks if a comma is trailing, handling comments properly.
     */
    private static boolean isTrailingCommaWithComments(String jsonc, int commaIndex) {
        int length = jsonc.length();
        boolean inString = false;
        boolean escaped = false;
        boolean inBlockComment = false;
        boolean inLineComment = false;
        
        // Look ahead from the comma to find the next significant character
        for (int i = commaIndex + 1; i < length; i++) {
            char current = jsonc.charAt(i);
            char next = (i + 1 < length) ? jsonc.charAt(i + 1) : '\0';
            
            // Track state from current position forward
            if (!inBlockComment && !inLineComment) {
                if (!inString) {
                    if (current == '"') {
                        inString = true;
                    } else if (current == '/' && next == '*') {
                        inBlockComment = true;
                        i++; // Skip the '*'
                        continue;
                    } else if (current == '/' && next == '/') {
                        inLineComment = true;
                        i++; // Skip the second '/'
                        continue;
                    } else if (current == '}' || current == ']') {
                        // Found closing bracket after comma - this is a trailing comma
                        return true;
                    } else if (!Character.isWhitespace(current)) {
                        // Found non-whitespace character that's not a closing bracket
                        return false;
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
                }
            } else if (inBlockComment) {
                if (current == '*' && next == '/') {
                    inBlockComment = false;
                    i++; // Skip the '/'
                    continue;
                }
            } else if (inLineComment) {
                if (current == '\n' || current == '\r') {
                    inLineComment = false;
                }
            }
        }
        
        // If we reached the end without finding anything, it's not trailing
        return false;
    }
    
    /**
     * Removes both JSON comments and trailing commas while preserving strings and handling edge cases securely.
     * This is a combined operation that is more efficient than calling both methods separately.
     * 
     * @param jsonc JSON with comments and potential trailing commas
     * @return JSON string with comments and trailing commas removed
     * @throws IllegalArgumentException if input is null
     */
    public static String removeCommentsAndTrailingCommas(String jsonc) {
        if (jsonc == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (jsonc.isEmpty()) {
            return jsonc;
        }
        
        // First remove comments using the existing proven method
        String withoutComments = removeComments(jsonc);
        
        // Then remove trailing commas from the result
        return removeTrailingCommas(withoutComments);
    }
    
    /**
     * Checks if a comma is trailing when comments are being removed in-process.
     */
    private static boolean isTrailingCommaInCleanJson(String cleanSoFar, String original, int commaIndex) {
        // For the combined method, we need a simpler approach since we're processing comments simultaneously
        // We'll append the comma tentatively and check the rest of the string
        int length = original.length();
        
        // Look ahead from the comma to find the next significant character, skipping comments
        for (int i = commaIndex + 1; i < length; i++) {
            char current = original.charAt(i);
            char next = (i + 1 < length) ? original.charAt(i + 1) : '\0';
            
            // Skip comments
            if (current == '/' && next == '*') {
                i = skipBlockComment(original, i);
                continue;
            } else if (current == '/' && next == '/') {
                i = skipLineComment(original, i);
                continue;
            } else if (current == '}' || current == ']') {
                // Found closing bracket after comma (ignoring comments) - this is a trailing comma
                return true;
            } else if (!Character.isWhitespace(current)) {
                // Found non-whitespace character that's not a closing bracket
                return false;
            }
        }
        
        return false;
    }
    
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

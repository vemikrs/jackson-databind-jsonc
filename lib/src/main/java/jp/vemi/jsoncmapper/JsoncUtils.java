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
     * - End-of-line comments: {@code //} comment
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
    
    /**
     * Converts single-quoted strings to double-quoted strings.
     * Handles proper escaping and preserves content inside existing double-quoted strings.
     * 
     * @param json5 JSON5 content with potential single-quoted strings
     * @return JSON content with single quotes converted to double quotes
     * @throws IllegalArgumentException if input is null
     */
    public static String convertSingleQuotes(String json5) {
        if (json5 == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (json5.isEmpty()) {
            return json5;
        }
        
        StringBuilder result = new StringBuilder(json5.length());
        int length = json5.length();
        boolean inDoubleQuotedString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = json5.charAt(i);
            
            if (!inDoubleQuotedString) {
                if (current == '"') {
                    inDoubleQuotedString = true;
                    result.append(current);
                } else if (current == '\'') {
                    // Start of single-quoted string - convert to double quote
                    result.append('"');
                    i = convertSingleQuotedStringContent(json5, i + 1, result);
                } else {
                    result.append(current);
                }
            } else {
                // Inside double-quoted string
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    inDoubleQuotedString = false;
                }
                result.append(current);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Helper method to convert content inside a single-quoted string to double-quoted format.
     */
    private static int convertSingleQuotedStringContent(String json5, int startIndex, StringBuilder result) {
        int length = json5.length();
        boolean escaped = false;
        
        for (int i = startIndex; i < length; i++) {
            char current = json5.charAt(i);
            
            if (escaped) {
                escaped = false;
                result.append(current);
            } else if (current == '\\') {
                escaped = true;
                result.append(current);
            } else if (current == '\'') {
                // End of single-quoted string
                result.append('"');
                return i;
            } else if (current == '"') {
                // Need to escape double quotes inside single-quoted string
                result.append('\\');
                result.append(current);
            } else {
                result.append(current);
            }
        }
        
        // Unclosed single quote - append closing double quote
        result.append('"');
        return length - 1;
    }
    
    /**
     * Converts hexadecimal number literals to decimal format.
     * Handles 0x and 0X prefixes and preserves numbers inside strings.
     * 
     * @param json5 JSON5 content with potential hexadecimal numbers
     * @return JSON content with hexadecimal numbers converted to decimal
     * @throws IllegalArgumentException if input is null
     */
    public static String convertHexNumbers(String json5) {
        if (json5 == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (json5.isEmpty()) {
            return json5;
        }
        
        StringBuilder result = new StringBuilder(json5.length());
        int length = json5.length();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = json5.charAt(i);
            char next = (i + 1 < length) ? json5.charAt(i + 1) : '\0';
            
            if (!inString) {
                if (current == '"') {
                    inString = true;
                    result.append(current);
                } else if (current == '0' && (next == 'x' || next == 'X')) {
                    // Potential hex number
                    int hexEnd = findHexNumberEnd(json5, i + 2);
                    if (hexEnd > i + 2) {
                        // Valid hex number found
                        String hexStr = json5.substring(i + 2, hexEnd);
                        try {
                            long decimal = Long.parseLong(hexStr, 16);
                            result.append(decimal);
                            i = hexEnd - 1; // -1 because loop will increment
                        } catch (NumberFormatException e) {
                            // Invalid hex number, keep as is
                            result.append(current);
                        }
                    } else {
                        result.append(current);
                    }
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
     * Helper method to find the end of a hexadecimal number.
     */
    private static int findHexNumberEnd(String text, int startIndex) {
        int i = startIndex;
        int length = text.length();
        
        while (i < length) {
            char c = text.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                break;
            }
            i++;
        }
        
        return i;
    }
    
    /**
     * Removes explicit plus signs from positive numbers.
     * Preserves plus signs inside strings and in non-numeric contexts.
     * 
     * @param json5 JSON5 content with potential plus-prefixed numbers
     * @return JSON content with plus signs removed from numbers
     * @throws IllegalArgumentException if input is null
     */
    public static String removePlusFromNumbers(String json5) {
        if (json5 == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (json5.isEmpty()) {
            return json5;
        }
        
        StringBuilder result = new StringBuilder(json5.length());
        int length = json5.length();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = json5.charAt(i);
            
            if (!inString) {
                if (current == '"') {
                    inString = true;
                    result.append(current);
                } else if (current == '+') {
                    // Check if this is a plus before a number
                    if (isPlusBeforeNumber(json5, i)) {
                        // Skip the plus sign
                        continue;
                    } else {
                        result.append(current);
                    }
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
     * Helper method to check if a plus sign is before a valid number.
     * Validates the complete number pattern including scientific notation.
     */
    private static boolean isPlusBeforeNumber(String text, int plusIndex) {
        int nextIndex = plusIndex + 1;
        if (nextIndex >= text.length()) {
            return false;
        }
        
        char first = text.charAt(nextIndex);
        // Must start with digit or decimal point
        if (!((first >= '0' && first <= '9') || first == '.')) {
            return false;
        }
        
        // Validate the complete number pattern
        int i = nextIndex;
        boolean hasDigits = false;
        boolean hasDecimal = false;
        
        // Parse integer/decimal part
        while (i < text.length()) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigits = true;
                i++;
            } else if (c == '.' && !hasDecimal) {
                hasDecimal = true;
                i++;
            } else {
                break;
            }
        }
        
        // Must have at least one digit
        if (!hasDigits) {
            return false;
        }
        
        // Check for scientific notation (e/E followed by optional +/- and digits)
        if (i < text.length() && (text.charAt(i) == 'e' || text.charAt(i) == 'E')) {
            i++; // skip e/E
            if (i < text.length() && (text.charAt(i) == '+' || text.charAt(i) == '-')) {
                i++; // skip optional +/-
            }
            // Must have digits after e/E
            boolean hasExpDigits = false;
            while (i < text.length() && text.charAt(i) >= '0' && text.charAt(i) <= '9') {
                hasExpDigits = true;
                i++;
            }
            // If we found 'e/E', we must have digits after it
            if (!hasExpDigits) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Converts Infinity and NaN literals to JSON-compatible representations.
     * Infinity becomes null and NaN becomes null (JSON doesn't support these values).
     * 
     * @param json5 JSON5 content with potential Infinity and NaN literals
     * @return JSON content with Infinity and NaN converted to null
     * @throws IllegalArgumentException if input is null
     */
    public static String convertInfinityAndNaN(String json5) {
        if (json5 == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (json5.isEmpty()) {
            return json5;
        }
        
        StringBuilder result = new StringBuilder(json5.length());
        int length = json5.length();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = json5.charAt(i);
            
            if (!inString) {
                if (current == '"') {
                    inString = true;
                    result.append(current);
                } else if (current == 'I' && matchesWordAt(json5, i, "Infinity")) {
                    result.append("null");
                    i += "Infinity".length() - 1; // -1 because loop will increment
                } else if (current == 'N' && matchesWordAt(json5, i, "NaN")) {
                    result.append("null");
                    i += "NaN".length() - 1; // -1 because loop will increment
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
     * Helper method to check if a word matches at a given position.
     */
    private static boolean matchesWordAt(String text, int index, String word) {
        if (index + word.length() > text.length()) {
            return false;
        }
        
        // Check if the word matches
        for (int i = 0; i < word.length(); i++) {
            if (text.charAt(index + i) != word.charAt(i)) {
                return false;
            }
        }
        
        // Check that it's a complete word (not part of another identifier)
        int afterIndex = index + word.length();
        if (afterIndex < text.length()) {
            char afterChar = text.charAt(afterIndex);
            if (Character.isLetterOrDigit(afterChar) || afterChar == '_') {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Converts multiline strings to single-line JSON strings with proper escaping.
     * Only processes newlines that are inside JSON string values, preserving
     * JSON structure newlines.
     * 
     * @param json5 JSON5 content with potential multiline strings
     * @return JSON content with multiline strings converted
     * @throws IllegalArgumentException if input is null
     */
    public static String convertMultilineStrings(String json5) {
        if (json5 == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (json5.isEmpty()) {
            return json5;
        }
        
        StringBuilder result = new StringBuilder(json5.length());
        int length = json5.length();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = json5.charAt(i);
            
            if (!inString) {
                // Outside strings - preserve all characters including newlines
                if (current == '"') {
                    inString = true;
                }
                result.append(current);
            } else {
                // Inside string
                if (escaped) {
                    escaped = false;
                    result.append(current);
                } else if (current == '\\') {
                    escaped = true;
                    result.append(current);
                } else if (current == '"') {
                    inString = false;
                    result.append(current);
                } else if (current == '\r') {
                    // Convert carriage return to escaped \r
                    result.append("\\r");
                } else if (current == '\n') {
                    // Convert newline to escaped \n
                    result.append("\\n");
                } else {
                    result.append(current);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * Escapes unescaped control characters in strings for JSON compliance.
     * Handles common control characters like tab, newline, etc.
     * 
     * @param json5 JSON5 content with potential unescaped control characters
     * @return JSON content with control characters properly escaped
     * @throws IllegalArgumentException if input is null
     */
    public static String escapeControlChars(String json5) {
        if (json5 == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (json5.isEmpty()) {
            return json5;
        }
        
        StringBuilder result = new StringBuilder(json5.length());
        int length = json5.length();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < length; i++) {
            char current = json5.charAt(i);
            
            if (!inString) {
                if (current == '"') {
                    inString = true;
                }
                result.append(current);
            } else {
                // Inside string
                if (escaped) {
                    escaped = false;
                    result.append(current);
                } else if (current == '\\') {
                    escaped = true;
                    result.append(current);
                } else if (current == '"') {
                    inString = false;
                    result.append(current);
                } else if (current < 32 && current != '\t' && current != '\n' && current != '\r') {
                    // Escape control characters (except common ones like tab, newline)
                    result.append(String.format("\\u%04x", (int) current));
                } else {
                    result.append(current);
                }
            }
        }
        
        return result.toString();
    }
}

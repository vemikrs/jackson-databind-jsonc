package jp.vemi.jsoncmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * JsoncMapper extends Jackson's JsonMapper to handle JSONC (JSON with Comments) format.
 * 
 * <p>Supports block comments, line comments, and optional trailing comma removal.
 * Provides ReDoS protection and preserves comments inside JSON strings.
 * 
 * @since 1.0.0
 * @see JsoncUtils
 */
public class JsoncMapper extends JsonMapper {
    
    private final boolean removeTrailingCommas;
    private final boolean allowSingleQuotes;
    private final boolean allowHexNumbers;
    private final boolean allowPlusNumbers;
    private final boolean allowInfinityAndNaN;
    private final boolean allowMultilineStrings;
    private final boolean allowUnescapedControlChars;
    
    /**
     * Default constructor that creates a JsoncMapper without trailing comma removal.
     * 
     * <p>Supports block comments and line comments. Use Builder for trailing comma support.
     * 
     * @see Builder
     */
    public JsoncMapper() {
        this.removeTrailingCommas = false;
        this.allowSingleQuotes = false;
        this.allowHexNumbers = false;
        this.allowPlusNumbers = false;
        this.allowInfinityAndNaN = false;
        this.allowMultilineStrings = false;
        this.allowUnescapedControlChars = false;
    }
    
    /**
     * Package-private constructor for Builder pattern.
     * 
     * @param removeTrailingCommas if true, trailing commas will be automatically removed
     * @param allowSingleQuotes if true, single-quoted strings will be converted to double-quoted
     * @param allowHexNumbers if true, hexadecimal numbers will be converted to decimal
     * @param allowPlusNumbers if true, plus signs will be removed from positive numbers
     * @param allowInfinityAndNaN if true, Infinity and NaN will be handled
     * @param allowMultilineStrings if true, multiline strings will be converted
     * @param allowUnescapedControlChars if true, control characters will be escaped
     */
    JsoncMapper(boolean removeTrailingCommas, boolean allowSingleQuotes, boolean allowHexNumbers,
                boolean allowPlusNumbers, boolean allowInfinityAndNaN, boolean allowMultilineStrings,
                boolean allowUnescapedControlChars) {
        this.removeTrailingCommas = removeTrailingCommas;
        this.allowSingleQuotes = allowSingleQuotes;
        this.allowHexNumbers = allowHexNumbers;
        this.allowPlusNumbers = allowPlusNumbers;
        this.allowInfinityAndNaN = allowInfinityAndNaN;
        this.allowMultilineStrings = allowMultilineStrings;
        this.allowUnescapedControlChars = allowUnescapedControlChars;
    }
    
    /**
     * Builder class for configuring JsoncMapper options.
     */
    public static class Builder {
        private boolean removeTrailingCommas = false;
        private boolean allowSingleQuotes = false;
        private boolean allowHexNumbers = false;
        private boolean allowPlusNumbers = false;
        private boolean allowInfinityAndNaN = false;
        private boolean allowMultilineStrings = false;
        private boolean allowUnescapedControlChars = false;
        
        /**
         * Enable automatic removal of trailing commas in JSON objects and arrays.
         * This allows parsing of JSON5/JSONC format with trailing commas.
         * 
         * @param allowTrailingCommas true to enable trailing comma removal
         * @return this builder for method chaining
         */
        public Builder allowTrailingCommas(boolean allowTrailingCommas) {
            this.removeTrailingCommas = allowTrailingCommas;
            return this;
        }
        
        /**
         * Enable support for single-quoted strings.
         * Converts single-quoted strings to double-quoted JSON strings.
         * Example: 'text' becomes "text"
         * 
         * @param allowSingleQuotes true to enable single quote string support
         * @return this builder for method chaining
         */
        public Builder allowSingleQuotes(boolean allowSingleQuotes) {
            this.allowSingleQuotes = allowSingleQuotes;
            return this;
        }
        
        /**
         * Enable support for hexadecimal number literals.
         * Converts hexadecimal numbers to decimal format.
         * Example: 0xFF becomes 255
         * 
         * @param allowHexNumbers true to enable hexadecimal number support
         * @return this builder for method chaining
         */
        public Builder allowHexNumbers(boolean allowHexNumbers) {
            this.allowHexNumbers = allowHexNumbers;
            return this;
        }
        
        /**
         * Enable support for numbers with explicit plus signs.
         * Removes explicit plus signs from positive numbers.
         * Example: +123 becomes 123
         * 
         * @param allowPlusNumbers true to enable plus sign number support
         * @return this builder for method chaining
         */
        public Builder allowPlusNumbers(boolean allowPlusNumbers) {
            this.allowPlusNumbers = allowPlusNumbers;
            return this;
        }
        
        /**
         * Enable support for Infinity and NaN literals.
         * Converts JavaScript-style Infinity and NaN to JSON null or string representation.
         * 
         * @param allowInfinityAndNaN true to enable Infinity and NaN support
         * @return this builder for method chaining
         */
        public Builder allowInfinityAndNaN(boolean allowInfinityAndNaN) {
            this.allowInfinityAndNaN = allowInfinityAndNaN;
            return this;
        }
        
        /**
         * Enable support for multiline strings.
         * Converts multiline strings to single-line JSON strings with proper escaping.
         * 
         * @param allowMultilineStrings true to enable multiline string support
         * @return this builder for method chaining
         */
        public Builder allowMultilineStrings(boolean allowMultilineStrings) {
            this.allowMultilineStrings = allowMultilineStrings;
            return this;
        }
        
        /**
         * Enable support for unescaped control characters in strings.
         * Automatically escapes control characters for JSON compliance.
         * 
         * @param allowUnescapedControlChars true to enable unescaped control character support
         * @return this builder for method chaining
         */
        public Builder allowUnescapedControlChars(boolean allowUnescapedControlChars) {
            this.allowUnescapedControlChars = allowUnescapedControlChars;
            return this;
        }
        
        /**
         * Convenience method to enable or disable core JSON5 features at once.
         * This includes the most commonly used and stable JSON5 features:
         * <ul>
         *   <li>Trailing commas in objects and arrays</li>
         *   <li>Single-quoted strings</li>
         *   <li>Hexadecimal number literals</li>
         *   <li>Numbers with explicit plus signs</li>
         *   <li>Infinity and NaN literals</li>
         * </ul>
         * 
         * <p>Note: This does not enable multiline strings or unescaped control characters
         * as these features may interfere with regular JSON formatting. These can be
         * enabled separately if needed using the individual methods.
         * 
         * <p>Example usage:
         * <pre>{@code
         * // Enable core JSON5 features
         * JsoncMapper mapper = new JsoncMapper.Builder()
         *     .enableJson5Features(true)
         *     .build();
         * 
         * // Disable all core JSON5 features (equivalent to default JsoncMapper)
         * JsoncMapper mapper = new JsoncMapper.Builder()
         *     .enableJson5Features(false)
         *     .build();
         * }</pre>
         * 
         * @param enable true to enable core JSON5 features, false to disable them all
         * @return this builder for method chaining
         */
        public Builder enableJson5Features(boolean enable) {
            this.removeTrailingCommas = enable;
            this.allowSingleQuotes = enable;
            this.allowHexNumbers = enable;
            this.allowPlusNumbers = enable;
            this.allowInfinityAndNaN = enable;
            // Note: multiline strings and unescaped control chars are excluded
            // as they can interfere with standard JSON formatting
            return this;
        }
        
        /**
         * Build a new JsoncMapper with the configured options.
         * 
         * @return configured JsoncMapper instance
         */
        public JsoncMapper build() {
            return new JsoncMapper(removeTrailingCommas, allowSingleQuotes, allowHexNumbers, 
                                 allowPlusNumbers, allowInfinityAndNaN, allowMultilineStrings,
                                 allowUnescapedControlChars);
        }
    }
    
    /**
     * Preprocesses JSONC content by removing comments and optionally applying JSON5 transformations.
     * 
     * @param content the JSONC content to preprocess
     * @return processed JSON content
     */
    private String preprocessJsonc(String content) {
        String result = content;
        
        // Always remove comments first
        result = JsoncUtils.removeComments(result);
        
        // Apply JSON5 transformations in order
        if (allowSingleQuotes) {
            result = JsoncUtils.convertSingleQuotes(result);
        }
        
        if (allowHexNumbers) {
            result = JsoncUtils.convertHexNumbers(result);
        }
        
        if (allowPlusNumbers) {
            result = JsoncUtils.removePlusFromNumbers(result);
        }
        
        if (allowInfinityAndNaN) {
            result = JsoncUtils.convertInfinityAndNaN(result);
        }
        
        if (allowMultilineStrings) {
            result = JsoncUtils.convertMultilineStrings(result);
        }
        
        if (allowUnescapedControlChars) {
            result = JsoncUtils.escapeControlChars(result);
        }
        
        // Remove trailing commas last, after all other transformations
        if (removeTrailingCommas) {
            result = JsoncUtils.removeTrailingCommas(result);
        }
        
        return result;
    }
    
    /**
     * Parse JSONC (JSON with Comments) content into a Java object.
     * This method securely removes comments while preserving strings and
     * validates input for security.
     * 
     * @param content JSONC content string
     * @param valueType target class type
     * @return parsed object of type T
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if content is null
     */
    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content into a Java object using TypeReference.
     * 
     * @param content JSONC content string
     * @param valueTypeRef target type reference
     * @return parsed object of type T
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if content is null
     */
    @Override
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        
        if (valueTypeRef == null) {
            throw new IllegalArgumentException("Value type reference cannot be null");
        }
        
        // Convert TypeReference to JavaType and delegate to avoid double processing
        JavaType javaType = getTypeFactory().constructType(valueTypeRef);
        return readValue(content, javaType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content into a Java object using JavaType.
     * 
     * @param content JSONC content string
     * @param valueType target JavaType
     * @return parsed object of type T
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if content is null
     */
    @Override
    public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException, JsonMappingException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a File.
     * 
     * @param src source file containing JSONC content
     * @param valueType target class type
     * @return parsed object of type T
     * @throws IOException if file reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if file or valueType is null
     */
    @Override
    public <T> T readValue(File src, Class<T> valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source file cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readFileToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a File using TypeReference.
     * 
     * @param src source file containing JSONC content
     * @param valueTypeRef target type reference
     * @return parsed object of type T
     * @throws IOException if file reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if file or valueTypeRef is null
     */
    @Override
    public <T> T readValue(File src, TypeReference<T> valueTypeRef) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source file cannot be null");
        }
        
        if (valueTypeRef == null) {
            throw new IllegalArgumentException("Value type reference cannot be null");
        }
        
        String content = readFileToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueTypeRef);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a File using JavaType.
     * 
     * @param src source file containing JSONC content
     * @param valueType target JavaType
     * @return parsed object of type T
     * @throws IOException if file reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if file or valueType is null
     */
    @Override
    public <T> T readValue(File src, JavaType valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source file cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readFileToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a Reader.
     * 
     * @param src source reader containing JSONC content
     * @param valueType target class type
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if reader or valueType is null
     */
    @Override
    public <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source reader cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readReaderToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a Reader using TypeReference.
     * 
     * @param src source reader containing JSONC content
     * @param valueTypeRef target type reference
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if reader or valueTypeRef is null
     */
    @Override
    public <T> T readValue(Reader src, TypeReference<T> valueTypeRef) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source reader cannot be null");
        }
        
        if (valueTypeRef == null) {
            throw new IllegalArgumentException("Value type reference cannot be null");
        }
        
        String content = readReaderToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueTypeRef);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a Reader using JavaType.
     * 
     * @param src source reader containing JSONC content
     * @param valueType target JavaType
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if reader or valueType is null
     */
    @Override
    public <T> T readValue(Reader src, JavaType valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source reader cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readReaderToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from an InputStream.
     * 
     * @param src source InputStream containing JSONC content
     * @param valueType target class type
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if InputStream or valueType is null
     */
    @Override
    public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source InputStream cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readInputStreamToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from an InputStream using TypeReference.
     * 
     * @param src source InputStream containing JSONC content
     * @param valueTypeRef target type reference
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if InputStream or valueTypeRef is null
     */
    @Override
    public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source InputStream cannot be null");
        }
        
        if (valueTypeRef == null) {
            throw new IllegalArgumentException("Value type reference cannot be null");
        }
        
        String content = readInputStreamToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueTypeRef);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from an InputStream using JavaType.
     * 
     * @param src source InputStream containing JSONC content
     * @param valueType target JavaType
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if InputStream or valueType is null
     */
    @Override
    public <T> T readValue(InputStream src, JavaType valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source InputStream cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readInputStreamToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a URL.
     * 
     * @param src source URL containing JSONC content
     * @param valueType target class type
     * @return parsed object of type T
     * @throws IOException if URL reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if URL or valueType is null
     */
    @Override
    public <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source URL cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readUrlToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a URL using TypeReference.
     * 
     * @param src source URL containing JSONC content
     * @param valueTypeRef target type reference
     * @return parsed object of type T
     * @throws IOException if URL reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if URL or valueTypeRef is null
     */
    @Override
    public <T> T readValue(URL src, TypeReference<T> valueTypeRef) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source URL cannot be null");
        }
        
        if (valueTypeRef == null) {
            throw new IllegalArgumentException("Value type reference cannot be null");
        }
        
        String content = readUrlToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueTypeRef);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a URL using JavaType.
     * 
     * @param src source URL containing JSONC content
     * @param valueType target JavaType
     * @return parsed object of type T
     * @throws IOException if URL reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if URL or valueType is null
     */
    @Override
    public <T> T readValue(URL src, JavaType valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source URL cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = readUrlToString(src);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a byte array.
     * 
     * @param src source byte array containing JSONC content
     * @param valueType target class type
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if byte array or valueType is null
     */
    @Override
    public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source byte array cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = new String(src, StandardCharsets.UTF_8);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a byte array using TypeReference.
     * 
     * @param src source byte array containing JSONC content
     * @param valueTypeRef target type reference
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if byte array or valueTypeRef is null
     */
    @Override
    public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source byte array cannot be null");
        }
        
        if (valueTypeRef == null) {
            throw new IllegalArgumentException("Value type reference cannot be null");
        }
        
        String content = new String(src, StandardCharsets.UTF_8);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueTypeRef);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a byte array using JavaType.
     * 
     * @param src source byte array containing JSONC content
     * @param valueType target JavaType
     * @return parsed object of type T
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws JsonMappingException if mapping fails
     * @throws IllegalArgumentException if byte array or valueType is null
     */
    @Override
    public <T> T readValue(byte[] src, JavaType valueType) throws IOException, JsonProcessingException, JsonMappingException {
        if (src == null) {
            throw new IllegalArgumentException("Source byte array cannot be null");
        }
        
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        
        String content = new String(src, StandardCharsets.UTF_8);
        String json = preprocessJsonc(content);
        return super.readValue(json, valueType);
    }
    
    /**
     * Helper method to read a URL to String.
     */
    private String readUrlToString(URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            return readInputStreamToString(inputStream);
        }
    }
    
    /**
     * Parse JSONC (JSON with Comments) content into a JsonNode tree.
     * 
     * @param content JSONC content string
     * @return JsonNode tree
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IllegalArgumentException if content is null
     */
    @Override
    public JsonNode readTree(String content) throws JsonProcessingException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        
        String json = preprocessJsonc(content);
        return super.readTree(json);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a File into a JsonNode tree.
     * 
     * @param file source file containing JSONC content
     * @return JsonNode tree
     * @throws IOException if file reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IllegalArgumentException if file is null
     */
    @Override
    public JsonNode readTree(File file) throws IOException, JsonProcessingException {
        if (file == null) {
            throw new IllegalArgumentException("Source file cannot be null");
        }
        
        String content = readFileToString(file);
        String json = preprocessJsonc(content);
        return super.readTree(json);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a Reader into a JsonNode tree.
     * 
     * @param reader source reader containing JSONC content
     * @return JsonNode tree
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IllegalArgumentException if reader is null
     */
    @Override
    public JsonNode readTree(Reader reader) throws IOException, JsonProcessingException {
        if (reader == null) {
            throw new IllegalArgumentException("Source reader cannot be null");
        }
        
        String content = readReaderToString(reader);
        String json = preprocessJsonc(content);
        return super.readTree(json);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from an InputStream into a JsonNode tree.
     * 
     * @param inputStream source InputStream containing JSONC content
     * @return JsonNode tree
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IllegalArgumentException if InputStream is null
     */
    @Override
    public JsonNode readTree(InputStream inputStream) throws IOException, JsonProcessingException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Source InputStream cannot be null");
        }
        
        String content = readInputStreamToString(inputStream);
        String json = preprocessJsonc(content);
        return super.readTree(json);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a URL into a JsonNode tree.
     * 
     * @param url source URL containing JSONC content
     * @return JsonNode tree
     * @throws IOException if URL reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IllegalArgumentException if URL is null
     */
    @Override
    public JsonNode readTree(URL url) throws IOException, JsonProcessingException {
        if (url == null) {
            throw new IllegalArgumentException("Source URL cannot be null");
        }
        
        String content = readUrlToString(url);
        String json = preprocessJsonc(content);
        return super.readTree(json);
    }
    
    /**
     * Parse JSONC (JSON with Comments) content from a byte array into a JsonNode tree.
     * 
     * @param content source byte array containing JSONC content
     * @return JsonNode tree
     * @throws IOException if reading fails
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IllegalArgumentException if byte array is null
     */
    @Override
    public JsonNode readTree(byte[] content) throws IOException, JsonProcessingException {
        if (content == null) {
            throw new IllegalArgumentException("Source byte array cannot be null");
        }
        
        String contentStr = new String(content, StandardCharsets.UTF_8);
        String json = preprocessJsonc(contentStr);
        return super.readTree(json);
    }
    
    /**
     * Helper method to read a File to String.
     *
     * @param file the File to read
     * @return the contents of the file as a String
     * @throws IOException if an I/O error occurs
     */
    private String readFileToString(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            return readReaderToString(reader);
        }
    }
    
    /**
     * Helper method to read a Reader to String.
     */
    private String readReaderToString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[8192];
        int length;
        while ((length = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, length);
        }
        return sb.toString();
    }
    
    /**
     * Helper method to read an InputStream to String.
     */
    private String readInputStreamToString(InputStream inputStream) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            return readReaderToString(reader);
        }
    }
}

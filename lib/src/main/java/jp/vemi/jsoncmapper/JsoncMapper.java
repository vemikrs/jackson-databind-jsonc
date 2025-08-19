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

public class JsoncMapper extends JsonMapper {
    
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
        
        String json = JsoncUtils.removeComments(content);
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
        
        String json = JsoncUtils.removeComments(content);
        return super.readValue(json, valueTypeRef);
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
        
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(content);
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
        String json = JsoncUtils.removeComments(contentStr);
        return super.readTree(json);
    }
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

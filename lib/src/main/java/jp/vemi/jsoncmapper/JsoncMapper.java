package jp.vemi.jsoncmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

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
}

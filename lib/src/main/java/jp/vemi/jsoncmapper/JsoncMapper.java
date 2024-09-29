package jp.vemi.jsoncmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsoncMapper extends ObjectMapper {
    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
        String json = JsoncUtils.removeComments(content);
        return super.readValue(json, valueType);
    }
}

package jp.vemi.jsoncmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsoncMapper extends JsonMapper {
    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
        String json = JsoncUtils.removeComments(content);
        return super.readValue(json, valueType);
    }
}

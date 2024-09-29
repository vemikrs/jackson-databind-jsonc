package jp.vemi.jsoncmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsoncMapper extends ObjectMapper {
    public JsoncMapper() {
        super();
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws IOException {
        String json = JsoncUtils.removeComments(content);
        return super.readValue(json, valueType);
    }
}

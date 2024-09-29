package jp.vemi.jsoncmapper.test;

import jp.vemi.jsoncmapper.JsoncMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsoncMapperTest {
    @Test
    public void testReadValue() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\" }";
        MyClass result = mapper.readValue(jsonc, MyClass.class);
        assertEquals("value", result.getKey());
    }

    static class MyClass {
        private String key;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }
}

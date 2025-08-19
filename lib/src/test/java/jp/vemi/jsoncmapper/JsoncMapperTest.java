package jp.vemi.jsoncmapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.jupiter.api.Assertions.*;

public class JsoncMapperTest {
    @Test
    public void testReadValue() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\" }";
        MyClass result = mapper.readValue(jsonc, MyClass.class);
        assertEquals("value", result.getKey());
    }

    @Test
    public void testReadValueWithMultipleComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\", /* another comment */ \"key2\": \"value2\" }";
        MyClassWithTwoKeys result = mapper.readValue(jsonc, MyClassWithTwoKeys.class);
        assertEquals("value", result.getKey());
        assertEquals("value2", result.getKey2());
    }

    @Test
    public void testReadValueWithNestedComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": { /* nested comment */ \"nestedKey\": \"nestedValue\" } }";
        MyClassWithNestedKey result = mapper.readValue(jsonc, MyClassWithNestedKey.class);
        assertEquals("nestedValue", result.getKey().getNestedKey());
    }

    @Test
    public void testReadValueWithEmptyJson() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{}";
        MyClass result = mapper.readValue(jsonc, MyClass.class);
        assertNull(result.getKey());
    }

    @Test
    public void testReadValueWithInvalidJson() {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\" ";
        assertThrows(JsonProcessingException.class, () -> {
            mapper.readValue(jsonc, MyClass.class);
        });
    }

    @Test
    public void testNullInputValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((String) null, MyClass.class);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue("{}", (Class<MyClass>) null);
        });
    }

    static class MyClass {
        private String key;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }

    static class MyClassWithTwoKeys {
        private String key;
        private String key2;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getKey2() { return key2; }
        public void setKey2(String key2) { this.key2 = key2; }
    }

    static class MyClassWithNestedKey {
        private NestedKey key;
        public NestedKey getKey() { return key; }
        public void setKey(NestedKey key) { this.key = key; }
    }

    static class NestedKey {
        private String nestedKey;
        public String getNestedKey() { return nestedKey; }
        public void setNestedKey(String nestedKey) { this.nestedKey = nestedKey; }
    }
}
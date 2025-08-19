import jp.vemi.jsoncmapper.JsoncMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.*;
import java.util.List;
import java.util.Map;

public class IntegrationTest {
    public static void main(String[] args) throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        
        // Test String-based reading
        String jsonc = "{ /* comment */ \"name\": \"test\", \"value\": 42 }";
        Map<String, Object> result = mapper.readValue(jsonc, new TypeReference<Map<String, Object>>() {});
        System.out.println("String test: " + result);
        
        // Test byte array reading
        byte[] bytes = jsonc.getBytes();
        Map<String, Object> result2 = mapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
        System.out.println("Byte array test: " + result2);
        
        // Test Reader-based reading
        try (StringReader reader = new StringReader(jsonc)) {
            Map<String, Object> result3 = mapper.readValue(reader, new TypeReference<Map<String, Object>>() {});
            System.out.println("Reader test: " + result3);
        }
        
        System.out.println("All integration tests passed!");
    }
}
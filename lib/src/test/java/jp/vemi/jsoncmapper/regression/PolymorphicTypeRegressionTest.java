package jp.vemi.jsoncmapper.regression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import jp.vemi.jsoncmapper.JsoncMapper;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/** CVE-2026-110879. Probes only increment a counter: no file/network/process effects. */
public class PolymorphicTypeRegressionTest {
    public static final class StringMapHolder { public Map<String, String> map; }
    public static final class ArrayHolder { public Serializable value; }
    public static class HarmlessProbe {
        static final AtomicInteger constructed = new AtomicInteger();
        public String data;
        public HarmlessProbe() { constructed.incrementAndGet(); }
        public HarmlessProbe(String ignored) { constructed.incrementAndGet(); }
    }

    private JsoncMapper mapMapper() {
        JsoncMapper mapper = new JsoncMapper();
        mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Map.class).build(), ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }

    private JsoncMapper arrayMapper() {
        JsoncMapper mapper = new JsoncMapper();
        mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Serializable.class).build(), ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }

    @Test void genericMapKeyCannotBypassBaseTypeApproval() {
        HarmlessProbe.constructed.set(0);
        String json = "{/*jsonc*/\"map\":[\"java.util.HashMap<" + HarmlessProbe.class.getName()
                + ",java.lang.String>\",{\"probe\":\"value\"}]}";
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> mapMapper().readValue(json, StringMapHolder.class));
        assertTrue(error.getMessage().contains(HarmlessProbe.class.getName()));
        assertEquals(0, HarmlessProbe.constructed.get(), "Rejected before constructor invocation");
    }

    @Test void arrayElementCannotBypassSerializableBaseApproval() {
        HarmlessProbe.constructed.set(0);
        String json = "{\"value\":[\"[L" + HarmlessProbe.class.getName() + ";\",[{\"data\":\"probe\"}]]}";
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> arrayMapper().readValue(json, ArrayHolder.class));
        assertTrue(error.getMessage().contains(HarmlessProbe.class.getName()));
        assertEquals(0, HarmlessProbe.constructed.get());
    }

    @Test void legitimateGenericStringMapStillDeserializes() throws Exception {
        String json = "{\"map\":[\"java.util.HashMap<java.lang.String,java.lang.String>\",{\"key\":\"value\"}]}";
        assertEquals("value", mapMapper().readValue(json, StringMapHolder.class).map.get("key"));
    }

    @Test void legitimateStringArrayStillDeserializes() throws Exception {
        ArrayHolder result = arrayMapper().readValue("{\"value\":[\"[Ljava.lang.String;\",[\"safe\"]]}", ArrayHolder.class);
        assertArrayEquals(new String[] { "safe" }, (String[]) result.value);
    }
}

package jp.vemi.jsoncmapper.regression;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import jp.vemi.jsoncmapper.JsoncMapper;

import java.io.Serializable;
import java.util.Map;

/** Launched in fresh JVMs against each actual distribution, without JUnit/test classpath. */
public final class ArtifactSmoke {
    public static final class MapHolder { public Map<String, String> map; }
    public static final class ArrayHolder { public Serializable value; }
    public static class Probe {
        static int calls;
        public String data;
        public Probe() { calls++; }
        public Probe(String ignored) { calls++; }
    }
    public static class Self {
        @JsonValue public Self value() { return this; }
    }

    public static void main(String[] args) throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        require("2.22.4".equals(mapper.version().toString()), "Wrong databind version: " + mapper.version());
        require(JsoncMapper.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(args[0]),
                "JsoncMapper was not loaded from the expected artifact");
        Map<String, Integer> value = mapper.readValue("{/*comment*/\"number\":7}",
                new TypeReference<Map<String, Integer>>() { });
        require(Integer.valueOf(7).equals(value.get("number")), "JSONC read failed");
        require(value.equals(mapper.readValue(mapper.writeValueAsString(value),
                new TypeReference<Map<String, Integer>>() { })), "Public API round trip failed");
        try {
            mapper.writeValueAsString(new Self());
            throw new AssertionError("Self cycle accepted");
        } catch (JsonMappingException expected) { /* StackOverflowError must not escape. */ }

        JsoncMapper mapMapper = new JsoncMapper();
        mapMapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Map.class).build(), ObjectMapper.DefaultTyping.NON_FINAL);
        try {
            mapMapper.readValue("{\"map\":[\"java.util.HashMap<" + Probe.class.getName()
                    + ",java.lang.String>\",{\"probe\":\"value\"}]}", MapHolder.class);
            throw new AssertionError("Unapproved map key accepted");
        } catch (InvalidTypeIdException expected) {
            require(expected.getMessage().contains(Probe.class.getName()), "Unexpected map rejection");
        }
        JsoncMapper arrayMapper = new JsoncMapper();
        arrayMapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Serializable.class).build(), ObjectMapper.DefaultTyping.NON_FINAL);
        try {
            arrayMapper.readValue("{\"value\":[\"[L" + Probe.class.getName()
                    + ";\",[{\"data\":\"probe\"}]]}", ArrayHolder.class);
            throw new AssertionError("Unapproved array element accepted");
        } catch (InvalidTypeIdException expected) {
            require(expected.getMessage().contains(Probe.class.getName()), "Unexpected array rejection");
        }
        require(Probe.calls == 0, "Rejected types were instantiated");
        System.out.println("PASS " + args[0] + " on Java " + System.getProperty("java.version")
                + "; databind=" + mapper.version() + "; core=" + mapper.getFactory().version());
    }

    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}

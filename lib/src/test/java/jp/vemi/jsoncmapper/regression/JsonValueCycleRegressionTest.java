package jp.vemi.jsoncmapper.regression;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;
import jp.vemi.jsoncmapper.JsoncMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

/** jackson-databind #6206: cycles must not escape as StackOverflowError. */
@Timeout(10)
public class JsonValueCycleRegressionTest {
    public static class SelfValue {
        @JsonValue public SelfValue value() { return this; }
    }
    public static class Link {
        public Link next;
        @JsonValue public Link value() { return next; }
    }
    public static class A {
        public B next;
        @JsonValue public B value() { return next; }
    }
    public static class B {
        public A next;
        @JsonValue public A value() { return next; }
    }
    public static class TypedSelf {
        @JsonValue
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public TypedSelf value() { return this; }
    }
    public static class ScalarValue {
        @JsonValue public String value() { return "normal"; }
    }

    @Test void directSelfReferenceIsMappingException() {
        assertThrows(JsonMappingException.class, () -> new JsoncMapper().writeValueAsString(new SelfValue()));
    }
    @Test void sameTypeMutualCycleIsMappingException() {
        Link a = new Link(); Link b = new Link(); a.next = b; b.next = a;
        assertThrows(JsonMappingException.class, () -> new JsoncMapper().writeValueAsString(a));
    }
    @Test void differentTypeMutualCycleIsMappingException() {
        A a = new A(); B b = new B(); a.next = b; b.next = a;
        assertThrows(JsonMappingException.class, () -> new JsoncMapper().writeValueAsString(a));
    }
    @Test void typedSelfReferenceIsMappingException() {
        assertThrows(JsonMappingException.class, () -> new JsoncMapper().writeValueAsString(new TypedSelf()));
    }
    @Test void nonCyclicJsonValueStillSerializes() throws Exception {
        assertEquals("\"normal\"", new JsoncMapper().writeValueAsString(new ScalarValue()));
    }
}

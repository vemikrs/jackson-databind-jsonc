package jp.vemi.jsoncmapper.test;

import jp.vemi.jsoncmapper.JsoncUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsoncUtilsTest {
    @Test
    public void testRemoveComments() {
        String jsonc = "{ /* comment */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }
}

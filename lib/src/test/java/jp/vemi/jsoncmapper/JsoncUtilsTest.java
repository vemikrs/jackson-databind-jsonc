package jp.vemi.jsoncmapper;

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

    @Test
    public void testNoComments() {
        String jsonc = "{ \"key\": \"value\" }";
        String expected = "{ \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testMultiLineComments() {
        String jsonc = "{ /* comment \n another line */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    // @Test
    // End of line comments are not supported (will delete the rest of the line)
    public void testEndOfLineComments() {
        String jsonc = "{ \"key\": \"value\" // comment \n }";
        String expected = "{ \"key\": \"value\" \n }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    // @Test
    // Nested comments are not supported
    public void testNestedComments() {
        String jsonc = "{ /* outer /* inner */ outer */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }
}
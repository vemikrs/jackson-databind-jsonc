package jp.vemi.jsoncmapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

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

    @Test
    public void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            JsoncUtils.removeComments(null);
        });
    }

    @Test 
    public void testEmptyInput() {
        String result = JsoncUtils.removeComments("");
        assertEquals("", result);
    }

    // Boundary Value Tests
    @Test
    public void testEmptyComment() {
        String jsonc = "{ /**/ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentAtStart() {
        String jsonc = "/* start comment */ { \"key\": \"value\" }";
        String expected = " { \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentAtEnd() {
        String jsonc = "{ \"key\": \"value\" } /* end comment */";
        String expected = "{ \"key\": \"value\" } ";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentAtStartAndEnd() {
        String jsonc = "/* start */ { \"key\": \"value\" } /* end */";
        String expected = " { \"key\": \"value\" } ";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    // Edge Case Tests
    @Test
    public void testConsecutiveComments() {
        String jsonc = "{ /* comment1 *//* comment2 */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testConsecutiveCommentsWithSpaces() {
        String jsonc = "{ /* comment1 */ /* comment2 */ \"key\": \"value\" }";
        String expected = "{   \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentsInArrays() {
        String jsonc = "[ /* comment */ \"item1\", /* comment */ \"item2\" ]";
        String expected = "[  \"item1\",  \"item2\" ]";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentsInComplexArrays() {
        String jsonc = "[ /* start */ { /* inner */ \"key\": \"value\" }, /* mid */ \"item\" /* end */ ]";
        String expected = "[  {  \"key\": \"value\" },  \"item\"  ]";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testOnlyComments() {
        String jsonc = "/* only */ /* comments */ /* here */";
        String expected = "  "; // Two spaces between the comments
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    // Unicode and Special Character Tests
    @Test
    public void testUnicodeInComments() {
        String jsonc = "{ /* 日本語コメント */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testSpecialCharactersInComments() {
        String jsonc = "{ /* Special chars: @#$%^&*()_+-={}[]|\\:;<>,.?/ */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testEmojiInComments() {
        String jsonc = "{ /* 🌟⭐✨💫🎉🎊 */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    // Performance Tests
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void testLargeFilePerformance() {
        StringBuilder largeJsonc = new StringBuilder();
        largeJsonc.append("{ ");
        
        // Create a large JSON with many comments
        for (int i = 0; i < 1000; i++) {
            largeJsonc.append("/* comment ").append(i).append(" */ ");
            largeJsonc.append("\"key").append(i).append("\": \"value").append(i).append("\"");
            if (i < 999) {
                largeJsonc.append(", ");
            }
        }
        largeJsonc.append(" }");
        
        String result = JsoncUtils.removeComments(largeJsonc.toString());
        assertNotNull(result);
        assertFalse(result.contains("/*"));
        assertFalse(result.contains("*/"));
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void testManySmallComments() {
        StringBuilder jsonc = new StringBuilder("{ ");
        for (int i = 0; i < 500; i++) {
            jsonc.append("/**/ ");
        }
        jsonc.append("\"key\": \"value\" }");
        
        String result = JsoncUtils.removeComments(jsonc.toString());
        assertNotNull(result);
        assertTrue(result.contains("\"key\": \"value\""));
    }

    // Additional Edge Cases
    @Test
    public void testCommentWithNewlines() {
        String jsonc = "{\n/* comment\nwith\nmultiple\nlines */\n\"key\": \"value\"\n}";
        String expected = "{\n\n\"key\": \"value\"\n}";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentWithCarriageReturns() {
        String jsonc = "{ /* comment\r\nwith\r\nCRLF */ \"key\": \"value\" }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentInStringValue() {
        // Comments inside strings should NOT be removed
        String jsonc = "{ \"message\": \"This /* is not a comment */ inside string\" }";
        String expected = "{ \"message\": \"This /* is not a comment */ inside string\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCommentInStringWithEscapes() {
        String jsonc = "{ \"path\": \"C:\\\\/* not a comment */\\\\folder\" }";
        String expected = "{ \"path\": \"C:\\\\/* not a comment */\\\\folder\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testEscapedQuotesInString() {
        String jsonc = "{ \"quote\": \"He said \\\"Hello /* comment */ World\\\"\" }";
        String expected = "{ \"quote\": \"He said \\\"Hello /* comment */ World\\\"\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }
}
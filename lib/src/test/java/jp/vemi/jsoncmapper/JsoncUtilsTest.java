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

    @Test
    public void testEndOfLineComments() {
        String jsonc = "{ \"key\": \"value\" // comment \n }";
        String expected = "{ \"key\": \"value\" \n }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testEndOfLineCommentsWithNewlinePreservation() {
        // Verify that newlines after line comments are preserved
        String jsonc = "{ \"key1\": \"value1\", // first comment\n  \"key2\": \"value2\" // second comment\n}";
        String expected = "{ \"key1\": \"value1\", \n  \"key2\": \"value2\" \n}";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testMultipleLineCommentsAcrossLines() {
        // Test multiple line comments on different lines
        String jsonc = "{\n  \"key1\": \"value1\", // comment 1\n  \"key2\": \"value2\", // comment 2\n  \"key3\": \"value3\" // comment 3\n}";
        String expected = "{\n  \"key1\": \"value1\", \n  \"key2\": \"value2\", \n  \"key3\": \"value3\" \n}";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testLineCommentsInsideStrings() {
        // Comments inside strings should NOT be removed - they are part of the string content
        String jsonc = "{ \"message\": \"This // is not a comment but part of the string\" }";
        String expected = "{ \"message\": \"This // is not a comment but part of the string\" }";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testLineCommentAtEndOfFile() {
        // Test line comment at the very end of the file without newline
        String jsonc = "{ \"key\": \"value\" } // final comment";
        String expected = "{ \"key\": \"value\" } ";
        String result = JsoncUtils.removeComments(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testLineCommentWithCarriageReturn() {
        // Test line comment ending with \r\n (Windows style)
        String jsonc = "{ \"key\": \"value\" // comment\r\n }";
        String expected = "{ \"key\": \"value\" \r\n }";
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

    // Trailing Comma Tests
    @Test
    public void testRemoveTrailingCommasFromObject() {
        String jsonc = "{ \"key1\": \"value1\", \"key2\": \"value2\", }";
        String expected = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testRemoveTrailingCommasFromArray() {
        String jsonc = "[ \"item1\", \"item2\", \"item3\", ]";
        String expected = "[ \"item1\", \"item2\", \"item3\" ]";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testRemoveTrailingCommasNestedStructures() {
        String jsonc = "{ \"array\": [ \"item1\", \"item2\", ], \"obj\": { \"nested\": \"value\", }, }";
        String expected = "{ \"array\": [ \"item1\", \"item2\" ], \"obj\": { \"nested\": \"value\" } }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testTrailingCommasInStrings() {
        // Commas inside strings should NOT be removed
        String jsonc = "{ \"message\": \"This, has, commas,\", }";
        String expected = "{ \"message\": \"This, has, commas,\" }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testNoTrailingCommas() {
        String jsonc = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
        String expected = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testTrailingCommasWithWhitespace() {
        String jsonc = "{ \"key\": \"value\",   \n  }";
        String expected = "{ \"key\": \"value\"   \n  }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testTrailingCommasWithComments() {
        String jsonc = "{ \"key\": \"value\", /* comment */ }";
        String expected = "{ \"key\": \"value\" /* comment */ }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testTrailingCommasWithLineComments() {
        String jsonc = "{ \"key\": \"value\", // comment\n }";
        String expected = "{ \"key\": \"value\" // comment\n }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testNonTrailingCommas() {
        // Regular commas should not be removed
        String jsonc = "{ \"a\": 1, \"b\": 2, \"c\": 3 }";
        String expected = "{ \"a\": 1, \"b\": 2, \"c\": 3 }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testEmptyObjectsAndArrays() {
        String jsonc = "{ \"empty_obj\": { }, \"empty_array\": [ ], }";
        String expected = "{ \"empty_obj\": { }, \"empty_array\": [ ] }";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    // Combined Comment and Trailing Comma Tests
    @Test
    public void testRemoveCommentsAndTrailingCommas() {
        String jsonc = "{ /* comment */ \"key\": \"value\", }";
        String expected = "{  \"key\": \"value\" }";
        String result = JsoncUtils.removeCommentsAndTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testCombinedComplexExample() {
        String jsonc = "{\n" +
            "    /* Main object */\n" +
            "    \"users\": [\n" +
            "        { \"name\": \"Alice\", \"age\": 30, }, // User 1\n" +
            "        { \"name\": \"Bob\", \"age\": 25, }, // User 2\n" +
            "    ],\n" +
            "    \"settings\": {\n" +
            "        /* Configuration */\n" +
            "        \"theme\": \"dark\",\n" +
            "        \"notifications\": true,\n" +
            "    },\n" +
            "}";
        String expected = "{\n" +
            "    \n" +
            "    \"users\": [\n" +
            "        { \"name\": \"Alice\", \"age\": 30 }, \n" +
            "        { \"name\": \"Bob\", \"age\": 25 } \n" +
            "    ],\n" +
            "    \"settings\": {\n" +
            "        \n" +
            "        \"theme\": \"dark\",\n" +
            "        \"notifications\": true\n" +
            "    }\n" +
            "}";
        String result = JsoncUtils.removeCommentsAndTrailingCommas(jsonc);
        assertEquals(expected, result);
    }

    @Test
    public void testTrailingCommasNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            JsoncUtils.removeTrailingCommas(null);
        });
    }

    @Test
    public void testCombinedNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            JsoncUtils.removeCommentsAndTrailingCommas(null);
        });
    }

    @Test
    public void testTrailingCommasEmptyString() {
        String jsonc = "";
        String expected = "";
        String result = JsoncUtils.removeTrailingCommas(jsonc);
        assertEquals(expected, result);
    }
}
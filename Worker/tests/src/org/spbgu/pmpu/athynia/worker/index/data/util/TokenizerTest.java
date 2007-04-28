package org.spbgu.pmpu.athynia.worker.index.data.util;


import junit.framework.JUnit4TestAdapter;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

import java.util.Arrays;

/**
 * User: Pisar Vasiliy
 */
public class TokenizerTest {
    @Test
    public void testEmptyTokenizer() throws Exception {
        String[] words = Tokenizer.tokenize("");
        assertTrue(words.length == 0);
    }

    //this hard-coded test looks ugly but it is really needed to avoid
    // some hard-to-find bugs in tokenizing text with regular expression
    @Test
    public void testSimpleTokenizerPattern() throws Exception {
        String[] words = Tokenizer.tokenize("test string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\nstring");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\rstring");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\tstring");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\bstring");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test,string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test;string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test.string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test/string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test?string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test'string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\"string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test!string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test@string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test#string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test$string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test%string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test^string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test&string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test*string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test(string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test)string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test-string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test=string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test|string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\\string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test:string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test+string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test_string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test>string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test<string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test\"string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test[string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("test]string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));
    }

    @Test
    public void testComplex() throws Exception {
        String[] words = Tokenizer.tokenize(" test\n\rstring\t*[]");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertEquals("Length equals", words.length, 2);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize(" test@#$ <>\"string");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "test") >= 0);
        assertTrue(Arrays.binarySearch(words, "string") >= 0);
        assertEquals("Length equals", words.length, 2);
        assertTrue(Arrays.deepEquals(new String[]{"string", "test"}, words));

        words = Tokenizer.tokenize("Once upon a time, a very long time ago, about last Friday...");
        Arrays.sort(words);
        assertTrue(Arrays.binarySearch(words, "time") >= 0);
        assertTrue(Arrays.binarySearch(words, "Friday") >= 0);
        assertEquals("Length equals", words.length, 12);
        assertTrue(Arrays.deepEquals(new String[]{"Friday", "Once", "a", "a", "about", "ago", "last", "long", "time", "time", "upon", "very"}, words));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TokenizerTest.class);
    }
}

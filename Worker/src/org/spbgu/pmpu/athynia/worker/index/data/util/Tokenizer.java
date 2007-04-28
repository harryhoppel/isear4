package org.spbgu.pmpu.athynia.worker.index.data.util;

import java.util.StringTokenizer;

/**
 * User: Pisar Vasiliy
 */
public class Tokenizer {
    private static final String DELIMITERS = " \n\r\t\b,;./?\'!@#$%^&*()-=|\\:+_><\"[]";

    public static String[] tokenize(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, DELIMITERS);
        int wordsCount = tokenizer.countTokens();
        String[] words = new String[wordsCount];
        for (int i = 0; i < words.length; i++) {
            words[i] = tokenizer.nextToken();
        }
        return words;
    }
}

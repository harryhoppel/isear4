package org.spbgu.pmpu.athynia.worker.index.data;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.spbgu.pmpu.athynia.worker.index.data.impl.DocumentUpdateImpl;

import java.util.Arrays;

/**
 * User: Pisar Vasiliy
 */
public class DocumentUpdateTest {
    @Test
    public void testLegalDocument() throws Exception {
        DocumentUpdate docUpdate = new DocumentUpdateImpl(
                "<document address=\"Test/address\">" +
                        "<entity>TeSt EnTiTy</entity>" +
                        "</document>");
        assertEquals("Address comparison", "Test/address", docUpdate.getAddress());
        assertEquals("Plain text comparison", "TeSt EnTiTy", docUpdate.getPlainText());

    }

    @Test
    public void testWords() throws Exception {
        DocumentUpdate docUpdate = new DocumentUpdateImpl(
                "<document address=\"Test/address\">" +
                        "<entity>test test test entity Entity</entity>" +
                        "</document>");
        String[] uniqueWords = docUpdate.getUniqueWords();
        Arrays.sort(uniqueWords);
        assertEquals("Number of unique words", 3, uniqueWords.length);
        assertEquals("Unique words", new String[]{"Entity", "entity", "test"}, uniqueWords);
        String[] words = docUpdate.getWords();
        Arrays.sort(words);
        assertEquals("Number of words comparison", 5, words.length);
        assertEquals("Words comparison", new String[]{"Entity", "entity", "test", "test", "test"}, words);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DocumentUpdateTest.class);
    }
}

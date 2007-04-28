package org.spbgu.pmpu.athynia.worker.index;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.spbgu.pmpu.athynia.worker.index.data.impl.DocumentUpdateImpl;
import org.spbgu.pmpu.athynia.worker.index.impl.MemoryIndex;

import java.util.Arrays;
import java.util.List;

/**
 * User: Pisar Vasiliy
 */
public class IndexTest {
    @Test
    public void testIndex() throws Exception {
        Index index = new MemoryIndex();
        index.write(
                new DocumentUpdateImpl("<document address=\"test/address1\">" +
                        "<entity>test document1</entity>" +
                        "</document>"),
                new DocumentUpdateImpl("<document address=\"test/address1\">" +
                        "<entity>test document2</entity>" +
                        "</document>"),
                new DocumentUpdateImpl("<document address=\"test/address2\">" +
                        "<entity>test document2</entity>" +
                        "</document>"));
        List<String> addresses = Arrays.asList(index.read("test"));
        assertTrue("Assert whether index contains right urls", addresses.contains("test/address1"));
        assertTrue("Assert whether index contains right urls", addresses.contains("test/address2"));
        addresses = Arrays.asList(index.read("document2"));
        assertTrue("Assert whether index contains right urls", addresses.contains("test/address1"));
        assertTrue("Assert whether index contains right urls", addresses.contains("test/address2"));
        addresses = Arrays.asList(index.read("document1"));
        assertTrue("Assert whether index contains right urls", addresses.contains("test/address1"));
        assertFalse("Assert whether index contains right urls", addresses.contains("test/address2"));
        List<String> emptyAddresses = Arrays.asList(index.read("fake"));
        assertEquals("Empty results equality", emptyAddresses.size(), 0);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(IndexTest.class);
    }
}

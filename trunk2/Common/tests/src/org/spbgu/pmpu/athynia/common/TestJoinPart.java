package org.spbgu.pmpu.athynia.common;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

/**
 * User: A.Selivanov
 * Date: 28.05.2007
 */
public class TestJoinPart {
    @Test
    public void testSerialization() {
        String key = "hello";
        String value = "world";
        int partNamuber = 0;
        int wholePartNumbers = 1;
        JoinPart joinPart = new JoinPartImpl(key, value, partNamuber, wholePartNumbers);
        JoinPart afterSerialize = new JoinPartImpl(joinPart.toBinaryForm());
        assertEquals(afterSerialize.getKey(), key);
        assertEquals(afterSerialize.getValue(), value);
        assertEquals(afterSerialize.getPartNumber(), partNamuber);
        assertEquals(afterSerialize.getWholePartsNumber(), wholePartNumbers);
    }

     public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestJoinPart.class);
    }
}

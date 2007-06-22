package org.spbgu.pmpu.athynia.worker.resource;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;
import org.spbgu.pmpu.athynia.worker.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * User: A.Selivanov
 * Date: 28.05.2007
 */
public class TestBTreeResourseManager extends TestCase {
    ResourceManager manager;

    public void tearDown() {
        if (manager != null) {
            manager.close();
        }
        System.gc();
        String testFileName = BTreeResourceManager.DEFAULT_DATABASE_FILE_NAME;
        FileUtil.deleteFile(new File(testFileName).toURI());
        FileUtil.deleteFile(new File(testFileName + ".db").toURI());
        FileUtil.deleteFile(new File(testFileName + ".lg").toURI());
    }

    public void testManagerInsert() throws IOException {
        String key = "hello";
        String value = "world";
        manager = new BTreeResourceManager();
        manager.write(key, value, 1, 2, 1000);
        manager.commit();
        manager = new BTreeResourceManager();
        JoinPart part = manager.search(key);
        assertEquals(part.getValue(), value);
    }

    public void testBasics() throws IOException {
        manager = new BTreeResourceManager();
        String test0, test1, test2;
        String value1, value2;

        test0 = "test0";
        test1 = "test1";
        test2 = "test2";
        value1 = "value1";
        value2 = "value2";

        manager.write(test1, value1);
        manager.write(test2, value2);
        manager.write(test2, value1);

        manager.commit();

        assertNull(manager.search(test0));
        assertEquals(manager.search(test1).getValue(), value1);
        assertEquals(manager.search(test2).getValue(), value1);
    }

    public void testAbort() {
        manager = new BTreeResourceManager();
        String test0, test1, test2;
        String value1, value2;

        test0 = "test0";
        test1 = "test1";
        test2 = "test2";
        value1 = "value1";
        value2 = "value2";

        manager.write(test1, value1);
        manager.write(test2, value2);
        assertNull(manager.search(test0));
        assertEquals(manager.search(test1).getValue(), value1);
        assertEquals(manager.search(test2).getValue(), value2);

        manager.commit();

        manager = new BTreeResourceManager();
        manager.write(test2, value1);
        manager.abort();

        assertNull(manager.search(test0));
        assertEquals(manager.search(test1).getValue(), value1);
        assertEquals(manager.search(test2).getValue(), value2);
    }

    public void testRemove() {
        manager = new BTreeResourceManager();
        String test0, test1, test2;
        String value1, value2;

        test0 = "test0";
        test1 = "test1";
        test2 = "test2";
        value1 = "value1";
        value2 = "value2";

        manager.write(test1, value1);
        manager.write(test2, value2);
        assertNull(manager.search(test0));
        assertEquals(manager.search(test1).getValue(), value1);
        assertEquals(manager.search(test2).getValue(), value2);

        manager.commit();

        manager = new BTreeResourceManager();
        manager.remove(test2);
        manager.commit();

        assertNull(manager.search(test0));
        assertEquals(manager.search(test1).getValue(), value1);
        assertNull(manager.search(test2));
    }

    public void testLargeData() {
        manager = new BTreeResourceManager();
        int size = manager.getSize();

        int iterations = 5000;

        for (int count = 0; count < iterations; count++) {
            manager.write("num" + count, String.valueOf(count));
        }
        manager.commit();
        for (int count = 0; count < iterations; count++) {
            assertEquals(String.valueOf(count), manager.search("num" + count).getValue());
        }

        for (int count = 0; count < iterations; count++) {
            assertEquals(String.valueOf(count), manager.remove("num" + count).getValue());
        }
        manager.commit();
        assertEquals(size, manager.getSize());
    }

    public void testMerge() {
        manager = new BTreeResourceManager();
        String key1, key2;
        String value1, value2;

        key1 = "test0";
        key2 = "test2";
        value1 = "value1";
        value2 = "value2";


        manager.write(key1, value1, 0, 5, 100);
        manager.commit();
        manager.merge(key1, value2, 1, 5, 100);
        manager.commit();
        JoinPart joinPart = manager.search(key1);
        assertEquals("value1 value2", joinPart.getValue());
        assertEquals(0, joinPart.getPartNumber());

        manager.merge(key2, value1, 4, 5, 100);
        manager.commit();
        manager.merge(key2, value2, 3, 5, 100);
        manager.commit();
        joinPart = manager.search(key2);
        assertEquals("value2 value1", joinPart.getValue());
        assertEquals(3, joinPart.getPartNumber());
    }
}

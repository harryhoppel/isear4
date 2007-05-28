package org.spbgu.pmpu.athynia.worker.resource;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.worker.util.FileUtil;

import java.io.IOException;

/**
 * User: A.Selivanov
 * Date: 28.05.2007
 */
public class TestBTreeResourseManager extends TestCase {
    public void tearDown() {
        System.gc();
        String testFileName = BTreeResourceManager.DEFAULT_DATABASE_FILE_NAME;
        FileUtil.deleteFile(testFileName);
        FileUtil.deleteFile(testFileName + ".db");
        FileUtil.deleteFile(testFileName + ".lg");
    }

    public void testManagerInsert() throws IOException {
        String key = "hello";
        String value = "world";
        ResourceManager manager = new BTreeResourceManager();
        manager.write(key, value, 1, 2, 1000);
        manager.commit();
        manager = new BTreeResourceManager();
        JoinPart part = manager.search(key);
        assertEquals(part.getValue(), value);
    }

    public void testBasics() throws IOException {
        ResourceManager manager = new BTreeResourceManager();
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
        ResourceManager manager = new BTreeResourceManager();
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
        ResourceManager manager = new BTreeResourceManager();
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
        ResourceManager manager = new BTreeResourceManager();
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
}

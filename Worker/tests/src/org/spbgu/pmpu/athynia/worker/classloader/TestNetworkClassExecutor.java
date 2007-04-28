package org.spbgu.pmpu.athynia.worker.classloader;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.worker.classloader.testServer.Server4Tests;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * User: Selivanov
 * Date: 20.04.2007
 */
public class TestNetworkClassExecutor extends TestCase {
    ClassExecutor executor;
    Server4Tests server;
    Thread thread;
    final Integer port = 5000;


    protected void setUp() throws Exception {
        server = new Server4Tests(port);
        thread = new Thread(server);
        thread.start();
    }

    public void testLocal() throws ClassNotFoundException, IOException {
        final String testString = "Hello world";
        executor = new NetworkClassExecutor(new File("tests/data/worker/classloader"), InetAddress.getLocalHost(), port);

        boolean result = executor.executeClass("org.spbgu.pmpu.athynia.worker.classloader.testclasses.Test1");
        assertTrue(result);

        result = !executor.executeClass("org.spbgu.pmpu.athynia.worker.classloader.testclasses.Test2");
        assertTrue(result);

        result = !executor.executeClass("org.spbgu.pmpu.athynia.worker.classloader.testclasses.None");
        assertTrue(result);

        server.setRequest(testString);
        result = executor.executeClass("org.spbgu.pmpu.athynia.worker.classloader.testclasses.Test3");
        assertTrue(result);
    }

//    public void testRemote() throws UnknownHostException {
//        executor = new NetworkClassExecutor(new File("tests/data/worker/classloader"), InetAddress.getLocalHost(), port);
//
//        boolean result = !executor.executeClass("org.spbgu.pmpu.athynia.worker.classloader.testclasses.None");
//        assertTrue(result);
//
//        result = executor.executeClass("org.spbgu.pmpu.athynia.central.classloader.testclasses.RemoteTest1");
//        assertTrue(result);
//    }
}
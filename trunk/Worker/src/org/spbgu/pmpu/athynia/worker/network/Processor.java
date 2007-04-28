package org.spbgu.pmpu.athynia.worker.network;

import org.spbgu.pmpu.athynia.worker.DataManager;
import org.spbgu.pmpu.athynia.worker.classloader.ClassExecutor;
import org.spbgu.pmpu.athynia.worker.classloader.NetworkClassExecutor;
import org.spbgu.pmpu.athynia.worker.index.Index;
import org.apache.log4j.Logger;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Selivanov
 * Date: 26.03.2007
 */
public class Processor implements Runnable {
    private static final Logger LOG = Logger.getLogger(Processor.class);
    protected final Index index = DataManager.getInstance().getData(Index.class);
    protected final List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
    ClassExecutor classExecutor;
    final String LOAD_CLASS_PREFIX = "loadClass:";

    public Processor() {
        this.classExecutor = new NetworkClassExecutor();
    }

    public void processData(Server server, SocketChannel socket, byte[] data, int count) {
        byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);
        synchronized (queue) {
            queue.add(new ServerDataEvent(server, socket, dataCopy));
            queue.notify();
        }
    }

    public void run() {
        ServerDataEvent dataEvent;
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        // interrupt
                    }
                }
                dataEvent = queue.remove(0);
                String request = new String(dataEvent.data);
                System.out.println(dataEvent.socket.socket().getPort());
                if (request.startsWith(LOAD_CLASS_PREFIX)) {
                    String className  =  request.substring(LOAD_CLASS_PREFIX.length() + 1);
                    LOG.info("WORKER:executeClass = " + className);
                    classExecutor.executeClass(className);
                }
            }
        }
    }
}

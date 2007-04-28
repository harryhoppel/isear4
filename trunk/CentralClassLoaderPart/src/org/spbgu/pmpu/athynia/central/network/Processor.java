package org.spbgu.pmpu.athynia.central.network;


import org.spbgu.pmpu.athynia.central.classloader.ZipClassReader;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Selivanov
 * Date: 26.03.2007
 */
public class Processor implements Runnable {
    protected final List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
    private ZipClassReader classReader;

    public Processor(ZipClassReader classReader) {
        this.classReader = classReader;
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
                System.out.println(request);
                dataEvent.server.send(dataEvent.socket, classReader.getClassFromCache(request));
            }
        }
    }
}


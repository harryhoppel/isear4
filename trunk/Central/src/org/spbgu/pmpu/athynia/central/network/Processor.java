package org.spbgu.pmpu.athynia.central.network;

import org.spbgu.pmpu.athynia.central.settings.Settings;
import org.spbgu.pmpu.athynia.central.DataManager;
import org.spbgu.pmpu.athynia.central.Central;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.net.MalformedURLException;

/**
 * User: Selivanov
 * Date: 26.03.2007
 */
public class Processor implements Runnable {
    protected final List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
    final Settings centralSettings = DataManager.getInstance().getData(Settings.class).childSettings("common").childSettings("central-server");
    public Central central;

    public Processor() {
        try {
            central = new Central();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
                dataEvent.server.send(dataEvent.socket, central.getClassAsByteArray(request));
            }
        }
    }
}


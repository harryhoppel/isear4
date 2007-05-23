package org.spbgu.pmpu.athynia.worker.broadcast;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class MainPortListener implements Runnable {
    private static final Logger LOG = Logger.getLogger(MainPortListener.class);

    private final int mainPort;
    private Socket connection;

    public MainPortListener(int mainPort) {
        this.mainPort = mainPort;
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(mainPort);
        } catch (IOException e) {
            LOG.error("Can't start listening on main port: " + mainPort, e);
        }
        if (serverSocket != null) {
            while (true) {
                Socket newConnection;
                try {
                    newConnection = serverSocket.accept();
                } catch (IOException e) {
                    LOG.error("I/O error while waiting for incoming connections", e);
                    synchronized (this) {
                        try {
                            wait(100);
                        } catch (InterruptedException e1) {/*ignore all interrupts*/}
                    }
                    continue;
                }
                synchronized (this) {
                    connection = newConnection;
                }
            }
        } else {
            LOG.error("Can't start listening on port: " + mainPort);
        }
    }

    public synchronized Socket getCentralConnection() {
        return connection;
    }
}

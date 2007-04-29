package org.spbgu.pmpu.athynia.central.communications;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.central.communications.impl.WorkerImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;

/**
 * User: vasiliy
 */
public class CentralMainPortListener implements Runnable {
    private static final Logger LOG = Logger.getLogger(CentralMainPortListener.class);

    private final ServerSocket centralMainSocket;
    private final WorkersManager workersManager;

    public CentralMainPortListener(int centralMainPort, WorkersManager workersManager) throws IllegalConfigException {
        this.workersManager = workersManager;
        try {
            centralMainSocket = new ServerSocket(centralMainPort);
        } catch (IOException e) {
            throw new IllegalConfigException("Can't open central server socket", e);
        }
    }

    public void run() {
        while (true) {
            Socket newConnection;
            try {
                newConnection = centralMainSocket.accept();
            } catch (IOException e) {
                LOG.error("I/O error while waiting for incoming connections", e);
                synchronized (this) {
                    try {
                        wait(100);
                    } catch (InterruptedException e1) {/*ignore all interrupts*/}
                }
                continue;
            }
            InetAddress newAddress = newConnection.getInetAddress();
            int newPort = newConnection.getPort();
            workersManager.addNewWorker(new WorkerImpl(new InetSocketAddress(newAddress, newPort), workersManager));
        }
    }
}

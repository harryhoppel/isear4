package org.spbgu.pmpu.athynia.central.network.communications;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.impl.WorkerImpl;
import org.spbgu.pmpu.athynia.common.settings.IllegalConfigException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
        LOG.info("Starting central main port listener on: " + centralMainSocket.getInetAddress() + ":" + centralMainSocket.getLocalPort());
        while (true) {
            Socket newConnection;
            int classloaderPort;
            int mainPort;
            InetAddress newAddress;
            try {
                newConnection = centralMainSocket.accept();
                byte[] buffer = new byte[256]; //enough to accept worker's address and port
                BufferedInputStream input = new BufferedInputStream(newConnection.getInputStream());
                input.read(buffer);
                String received = new String(buffer, "UTF-8");
                classloaderPort = Integer.parseInt(received.substring(0, received.indexOf(',')));
                mainPort = parseMainPort(received.substring(received.indexOf(',') + 1));
                LOG.debug("Received port number: " + classloaderPort);
                newAddress = newConnection.getInetAddress();
                input.close();
            } catch (IOException e) {
                LOG.error("I/O error while waiting for incoming connections", e);
                synchronized (this) {
                    try {
                        wait(100);
                    } catch (InterruptedException e1) {/*ignore all interrupts*/}
                }
                continue;
            }
            WorkerImpl worker = new WorkerImpl(new InetSocketAddress(newAddress, classloaderPort), mainPort, workersManager);
            boolean added = workersManager.addNewWorker(worker);
            if (!added) {
                try {
                    workersManager.replaceSocket(worker, newConnection);
                } catch (IOException e) {
                    LOG.warn("Can't replace worker's socket with a new one; worker: " + newAddress + ":" + classloaderPort);
                }
            }
        }
    }

    private int parseMainPort(String received) {
        StringBuffer buffer = new StringBuffer();
        int index = 0;
        while (Character.isDigit(received.charAt(index))) {
            index++;
            buffer.append(received.charAt(index));
        }
        return Integer.parseInt(buffer.toString());
    }

}

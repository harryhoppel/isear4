package org.spbgu.pmpu.athynia.central.network.communications;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.impl.WorkerImpl;
import org.spbgu.pmpu.athynia.common.settings.IllegalConfigException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
            int newPort;
            InetAddress newAddress;
            try {
                newConnection = centralMainSocket.accept();
                byte[] buffer = new byte[256]; //enough to accept worker's address and port
                BufferedInputStream input = new BufferedInputStream(newConnection.getInputStream());
                input.read(buffer);
                newPort = parseReceivedData(buffer);
                LOG.debug("Received port number: " + newPort);
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
            WorkerImpl worker = new WorkerImpl(new InetSocketAddress(newAddress, newPort), workersManager);
            boolean added = workersManager.addNewWorker(worker);
            if (!added) {
                try {
                    workersManager.replaceSocket(worker, newConnection);
                } catch (IOException e) {
                    LOG.warn("Can't replace worker's socket with a new one; worker: " + newAddress + ":" + newPort);
                }
            }
        }
    }

    private int parseReceivedData(byte[] data) {
        String dataString = null;
        try {
            dataString = new String(data, 0, 5, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // can't happen
            LOG.warn("Unsupported encoding", e);
        }
        LOG.debug("Trying to parse string: " + dataString);
        return Integer.parseInt(dataString);
    }
}

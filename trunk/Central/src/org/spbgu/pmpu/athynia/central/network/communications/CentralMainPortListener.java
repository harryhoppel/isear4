package org.spbgu.pmpu.athynia.central.network.communications;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.impl.WorkerImpl;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;
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

    public CentralMainPortListener(int centralMainPort, WorkersManager workersManager, SocketOpener socketOpener) throws IllegalConfigException {
        this.workersManager = workersManager;
        try {
            centralMainSocket = socketOpener.openServerSocketOnDefaultNic(centralMainPort);
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
                byte[] buffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8 * 2 + 1];
                BufferedInputStream input = new BufferedInputStream(newConnection.getInputStream());
                input.read(buffer);
                String received = new String(buffer, "UTF-8");
                classloaderPort = Integer.parseInt(deleteZeroes(received.substring(0, received.indexOf(','))));
                mainPort = Integer.parseInt(deleteZeroes(received.substring(received.indexOf(',') + 1)));
                LOG.debug("New incoming connection from worker:" + newConnection.getInetAddress() + ":" + newConnection.getPort() + "; received port number: " + classloaderPort);
                newAddress = newConnection.getInetAddress();
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
            workersManager.addNewWorker(worker);
        }
    }

    private String deleteZeroes(String s) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.substring(0, 1).equals("0") && buffer.length() > 1) {
            buffer.delete(0, 1);
        }
        return buffer.toString();
    }

}

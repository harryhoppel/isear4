package org.spbgu.pmpu.athynia.worker.network.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;
import org.spbgu.pmpu.athynia.worker.Worker;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class MainPortListener implements Runnable {
    private static final Logger LOG = Logger.getLogger(MainPortListener.class);

    private final int mainPort;
    private final SocketOpener socketOpener;

    private volatile Socket connection;

    public MainPortListener(int mainPort, SocketOpener socketOpener) {
        this.mainPort = mainPort;
        this.socketOpener = socketOpener;
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = socketOpener.openServerSocketOnDefaultNic(mainPort);
        } catch (IOException e) {
            LOG.error("Can't start listening on main port: " + mainPort, e);
        }
        if (serverSocket != null) {
            LOG.debug("Starting main worker socket listener/acceptor on: " + serverSocket.getInetAddress() + ":" + mainPort);
            while (true) {
                Socket newConnection;
                try {
                    newConnection = serverSocket.accept();
                    LOG.debug("New connection acquired: " + newConnection.getInetAddress() + ":" + newConnection.getPort());
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

    public synchronized Socket getCentralConnection() throws IOException {
        LOG.debug("Getting socket from worker main port listener");
        if (connection != null
                ) {
            return connection;
        } else {
            LOG.debug("Trying to connect to central: " + BroadcastListeningDaemon.centralAddress + ":" + BroadcastListeningDaemon.centralMainPort);
            connection = new Socket(BroadcastListeningDaemon.centralAddress, BroadcastListeningDaemon.centralMainPort);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write((getIntInUtf8(Worker.MAIN_WORKER_CLASSOADER_PORT) + "," + getIntInUtf8(mainPort)).getBytes("UTF-8"));
            outputStream.flush();
            return connection;
        }
    }

    public synchronized void closeCentralConnection() {
    }

    private String getIntInUtf8(int i) {
        StringBuffer buffer = new StringBuffer();
        String integer = Integer.toString(i);
        buffer.append(integer);
        while (buffer.length() < CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8) {
            buffer.insert(0, "0");
        }
        return buffer.toString();
    }

}

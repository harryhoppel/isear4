package org.spbgu.pmpu.athynia.central.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.communications.WorkersManager;
import org.spbgu.pmpu.athynia.central.communications.Worker;
import org.spbgu.pmpu.athynia.central.communications.impl.WorkerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;

/**
 * User: vasiliy
 */
class WorkersSignalGetter implements Runnable {
    private static final Logger LOG = Logger.getLogger(WorkersSignalGetter.class);

    private final ServerSocket socketToListen;

    private final WorkersManager workersManager;

    public WorkersSignalGetter(int portToListen, InetAddress inetAddress, WorkersManager workersManager) throws IOException {
        this.workersManager = workersManager;
        this.socketToListen = new ServerSocket(portToListen, 0, inetAddress);
    }

    public void run() {
        while (true) {
            Socket socket = null;
            try {
                socket = socketToListen.accept();
                InetAddress detectedAddress = socket.getInetAddress();
                InputStream inputFromWorker = socket.getInputStream();
                byte[] buffer = new byte[256]; //size is surely enough to accept worker's main port number
                int bytesRead = inputFromWorker.read(buffer);
                int detectedPort = veryDumbMethodToParseUnknownIntegers(new String(buffer, 0, bytesRead, "US-ASCII"));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("New address detected: " + detectedAddress.toString() + ":" + detectedPort);
                }
                Worker newWorker = new WorkerImpl(new InetSocketAddress(detectedAddress, detectedPort), workersManager);
                workersManager.addNewWorker(newWorker);
            } catch (IOException e) {
                LOG.error("Can't communicate with worker", e);
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    LOG.error("Can't close incoming connection", e);
                }
            }
        }
    }

    private int veryDumbMethodToParseUnknownIntegers(String integerFollowedByMess) {
        int result = 0;
        for (int i = integerFollowedByMess.length(); i > 0; i--) {
            String temp = integerFollowedByMess.substring(0, i);
            try {
                result = Integer.parseInt(temp);
                break;
            } catch (NumberFormatException e) {/*skip one step*/}
        }
        return result;
    }
}

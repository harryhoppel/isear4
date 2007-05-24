package org.spbgu.pmpu.athynia.central.network.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.impl.WorkerImpl;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;

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

    public WorkersSignalGetter(int portToListen, SocketOpener socketOpener, WorkersManager workersManager) throws IOException {
        this.workersManager = workersManager;
        this.socketToListen = socketOpener.openServerSocketOnDefaultNic(portToListen);
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
                int detectedPort = veryDumbMethodToParseUnknownIntegers(new String(buffer, 0, bytesRead, "UTF-8"));
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

package org.spbgu.pmpu.athynia.central.network.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.impl.WorkerImpl;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
            try {
                Socket socket = socketToListen.accept();
                InetAddress detectedAddress = socket.getInetAddress();
                InputStream inputFromWorker = socket.getInputStream();
                byte[] buffer = new byte[256]; //size is surely enough to accept worker's main port number
                int bytesRead = inputFromWorker.read(buffer);
                String received = new String(buffer, 0, bytesRead, "UTF-8");
                int mainPort = parseIntegerFollowedByMess(received.substring(received.indexOf(',') + 1, received.length()));
                int classloaderPort = Integer.parseInt(received.substring(0, received.indexOf(',')));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("New address detected: " + detectedAddress.toString() + ":" + mainPort);
                }
                Worker newWorker = new WorkerImpl(new InetSocketAddress(detectedAddress, classloaderPort), mainPort, workersManager);
                workersManager.addNewWorker(newWorker);
            } catch (IOException e) {
                LOG.error("Can't communicate with worker", e);
            }
        }
    }

    private int parseIntegerFollowedByMess(String intFollowedByMess) {
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < intFollowedByMess.length(); i++) {
            char nextChar = intFollowedByMess.charAt(i);
            if (Character.isDigit(nextChar)) {
                temp.append(nextChar);
            } else {
                break;
            }
        }
        return Integer.parseInt(temp.toString());
    }
}

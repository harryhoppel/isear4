package org.spbgu.pmpu.athynia.central.network.communications.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.communications.WorkerConnectionsManager;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: vasiliy
 */
public class WorkerConnectionsManagerImpl implements WorkerConnectionsManager {
    private static final Logger LOG = Logger.getLogger(WorkerConnectionsManagerImpl.class);

    private final SocketOpener socketOpener;

    private final Map<Worker, Socket> openedSockets = Collections.synchronizedMap(new HashMap<Worker, Socket>());

    public WorkerConnectionsManagerImpl(SocketOpener socketOpener) {
        this.socketOpener = socketOpener;
    }

    public Socket getSocket(Worker worker) throws IOException {
        LOG.debug("Acquiring socket for worker: " + worker.getFullAddress());
//        try {
//            Thread.sleep(1000); //debug...
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        Socket ret = openedSockets.get(worker);
        if (ret == null) {
            ret = socketOpener.openSocketOnDefaultNic(worker.getFullAddress().getHostName(), worker.getMainPort());
            openedSockets.put(worker, ret);
        }
        LOG.debug("Socket acquired for worker: " + ret.getInetAddress() + ":" + ret.getPort());
        return ret;
    }

    public synchronized void closeSocket(Worker worker) throws IOException {
        Socket socketToClose = openedSockets.remove(worker);
        LOG.debug("Trying to close socket: " + socketToClose.getInetAddress() + ":" + socketToClose.getPort());
        socketToClose.close();
        LOG.debug("Socket was closed");
    }

    public void replaceWorkerConnection(Worker worker, Socket newConnection) throws IOException {
        Socket socketToreplace = openedSockets.remove(worker);
        if (socketToreplace != null) {
            socketToreplace.close();
        }
        LOG.debug("Replacing worker's socket; worker: " + worker.getFullAddress() + ", new socket: " + newConnection.getInetAddress() + ":" + newConnection.getPort());
        openedSockets.put(worker, newConnection);
    }
}

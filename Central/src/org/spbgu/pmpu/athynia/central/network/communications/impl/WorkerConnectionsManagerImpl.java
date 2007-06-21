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
        Socket ret = openedSockets.get(worker);
        if (ret == null) {
            LOG.debug("Opening new socket to worker...");
            ret = socketOpener.openSocketOnDefaultNic(worker.getFullAddress().getHostName(), worker.getMainPort());
            openedSockets.put(worker, ret);
        }
        LOG.debug("Socket acquired for worker: " + ret.getInetAddress() + ":" + ret.getPort());
        return ret;
    }
}

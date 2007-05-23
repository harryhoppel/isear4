package org.spbgu.pmpu.athynia.central.network.communications.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.communications.WorkerConnectionsManager;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * User: vasiliy
 */
public class WorkerConnectionsManagerImpl implements WorkerConnectionsManager {
    private final SocketOpener socketOpener;

    private final Map<Worker, Socket> openedSockets = new HashMap<Worker, Socket>();

    public WorkerConnectionsManagerImpl(SocketOpener socketOpener) {
        this.socketOpener = socketOpener;
    }

    public Socket getSocket(Worker worker) throws IOException {
        Socket ret;
        ret = socketOpener.openSocketOnDefaultNic(worker.getFullAddress().getHostName(), worker.getFullAddress().getPort());
        openedSockets.put(worker, ret);
        return ret;
    }

    public void closeSocket(Worker worker) throws IOException {
        Socket socketToClose = openedSockets.remove(worker);
        if (!socketToClose.isClosed()) {
            socketToClose.close();
        }
    }
}

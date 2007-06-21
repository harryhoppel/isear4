package org.spbgu.pmpu.athynia.central.network.communications;

import org.spbgu.pmpu.athynia.central.network.Worker;

import java.net.Socket;
import java.io.IOException;

/**
 * User: vasiliy
 */
public interface WorkerConnectionsManager {
    Socket getSocket(Worker worker) throws IOException;
    void closeSocket(Worker worker) throws IOException;

    void replaceWorkerConnection(Worker worker, Socket newConnection) throws IOException;
}

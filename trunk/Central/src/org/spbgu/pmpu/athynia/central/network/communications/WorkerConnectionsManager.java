package org.spbgu.pmpu.athynia.central.network.communications;

import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.communications.impl.WorkerConnectionsManagerImpl;
import org.spbgu.pmpu.athynia.common.network.impl.SocketOpenerImpl;

import java.net.Socket;
import java.io.IOException;

/**
 * User: vasiliy
 */
public interface WorkerConnectionsManager {
    Socket getSocket(Worker worker) throws IOException;
    void closeSocket(Worker worker) throws IOException;
}

package org.spbgu.pmpu.athynia.central.network.communications;

import org.spbgu.pmpu.athynia.central.network.Worker;

import java.io.IOException;
import java.net.Socket;

/**
 * User: vasiliy
 */
public interface WorkerConnectionsManager {
    Socket getSocket(Worker worker) throws IOException;
}

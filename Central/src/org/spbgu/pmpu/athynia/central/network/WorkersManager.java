package org.spbgu.pmpu.athynia.central.network;

import org.spbgu.pmpu.athynia.common.network.SocketOpener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

/**
 * User: vasiliy
 */
public interface WorkersManager {
    /**
     * @param socketOpener - controls the NIC, where new sockets will ne opened
     * @throws IllegalStateException - if called more than once
     */
    void setupWorkerConnectionsManager(SocketOpener socketOpener) throws IllegalStateException;

    /**
     * @return set of workers, that is safe(!) to modify(that set is a copy(!) of workers).
     *         It also means that two invocations of that method may return two different sets.
     */
    Set<Worker> getAll();

    /**
     * @param worker - worker to add
     * @return true if worker was really new worker (i.e., unknown for this workers manager).
     */
    boolean addNewWorker(Worker worker);

    Worker findWorker(InetAddress inetAddress, int port);
    Worker findWorker(InetSocketAddress address);

    Socket openSocket(Worker worker) throws IOException;
}

package org.spbgu.pmpu.athynia.central.communications;

import org.spbgu.pmpu.athynia.central.communications.impl.WorkerImpl;

import java.util.Set;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: vasiliy
 */
public interface WorkersManager {
    /**
     *
     * @return set of workers, that is safe(!) to modify(that set is a copy(!) of workers).
     * It also means that two invocations of that method may return two different sets.
     */
    Set<Worker> getAll();

    boolean isAlive(Worker worker);

    /**
     *
     * @return true if worker was really new worker (i.e., unknown for this workers manager).
     * @param worker - worker to add
     */
    boolean addNewWorker(Worker worker);

    Worker findWorker(InetAddress inetAddress, int port);
    Worker findWorker(InetSocketAddress address);

    Socket getSocket(Worker worker);
}

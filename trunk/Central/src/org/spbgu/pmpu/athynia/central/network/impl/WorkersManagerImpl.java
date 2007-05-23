package org.spbgu.pmpu.athynia.central.network.impl;

import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.communications.WorkerConnectionsManager;
import org.spbgu.pmpu.athynia.central.network.communications.impl.WorkerConnectionsManagerImpl;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class WorkersManagerImpl implements WorkersManager {
    private static final WorkersManager INSTANCE = new WorkersManagerImpl();
    private WorkerConnectionsManager workerConnectionsManager;

    public static WorkersManager getInstance() {
        return INSTANCE;
    }

    private final Set<Worker> workers;
    private final Map<InetSocketAddress, Worker> workersAddresses;

    private WorkersManagerImpl() {
        workers = Collections.synchronizedSet(new HashSet<Worker>());
        workersAddresses = Collections.synchronizedMap(new HashMap<InetSocketAddress, Worker>());
    }

    public void setupWorkerConnectionsManager(SocketOpener socketOpener) throws IllegalStateException {
        if (this.workerConnectionsManager != null) throw new IllegalStateException("Can't assign second socket opener to workers manager");
        workerConnectionsManager = new WorkerConnectionsManagerImpl(socketOpener);
    }

    public Set<Worker> getAll() {
        Set<Worker> returnCopy = new HashSet<Worker>();
        for (Worker worker : workers) {
            returnCopy.add(worker);
        }
        return returnCopy;
    }

    public boolean addNewWorker(Worker worker) {
        if (workers.contains(worker)) {
            return false;
        }
        workers.add(worker);
        workersAddresses.put(worker.getFullAddress(), worker);
        return true;
    }

    public Worker findWorker(InetAddress inetAddress, int port) {
        return findWorker(new InetSocketAddress(inetAddress, port));
    }

    public Worker findWorker(InetSocketAddress address) {
        return workersAddresses.get(address);
    }

    public Socket openSocket(Worker worker) throws IOException {
        return workerConnectionsManager.getSocket(worker);
    }

    public void closeSocket(Worker worker) throws IOException {
        workerConnectionsManager.closeSocket(worker);
    }
}

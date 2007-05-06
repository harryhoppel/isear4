package org.spbgu.pmpu.athynia.central.communications.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.communications.Worker;
import org.spbgu.pmpu.athynia.central.communications.WorkersManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: vasiliy
 */
public class WorkersManagerImpl implements WorkersManager {
    private static final Logger LOG = Logger.getLogger(WorkersManagerImpl.class);
    private static final WorkersManager instance = new WorkersManagerImpl();

    public static WorkersManager getInstance() {
        return instance;
    }

    private final Set<Worker> workers;
    private final Map<InetSocketAddress, Worker> workersAddresses;
    private final Map<Worker, Socket> workersSockets;

    private WorkersManagerImpl() {
        workers = Collections.synchronizedSet(new HashSet<Worker>());
        workersAddresses = Collections.synchronizedMap(new HashMap<InetSocketAddress, Worker>());
        workersSockets = Collections.synchronizedMap(new HashMap<Worker, Socket>());
    }

    public Set<Worker> getAll() {
        Set<Worker> returnCopy = new HashSet<Worker>();
        for (Worker worker : workers) {
            returnCopy.add(worker);
        }
        return returnCopy;
    }

    public boolean isAlive(Worker worker) {
        Socket workerSocket = workersSockets.get(worker);
        return !workerSocket.isClosed() && workerSocket.isConnected();
    }

    public boolean addNewWorker(Worker worker) {
        if (workers.contains(worker)) {
            return false;
        }
        workers.add(worker);
//todo:        workersAddresses.put(new InetSocketAddress(worker.getSocket().getInetAddress(), worker.getSocket().getPort()), worker);
        workersAddresses.put(worker.getFullAddress(), worker);
        return true;
    }

    public Worker findWorker(InetAddress inetAddress, int port) {
        return findWorker(new InetSocketAddress(inetAddress, port));
    }

    public Worker findWorker(InetSocketAddress address) {
        return workersAddresses.get(address);
    }

    public Socket getSocket(Worker worker) {
        Socket socket = workersSockets.get(worker);
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            return socket;
        } else {
            try {
                InetSocketAddress workerAddress = worker.getFullAddress();
                socket = new Socket(workerAddress.getAddress(), workerAddress.getPort());
                workersSockets.put(worker, socket);
                return socket;
            } catch (IOException e) {
                LOG.error("Cannot create socket on " + worker.getFullAddress(), e);
                return null;
            }
        }
    }
}

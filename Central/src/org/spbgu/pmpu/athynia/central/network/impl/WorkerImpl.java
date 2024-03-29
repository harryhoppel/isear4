package org.spbgu.pmpu.athynia.central.network.impl;

import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class WorkerImpl implements Worker {
    private final InetSocketAddress address;
    private final WorkersManager manager;
    private final int mainPort;

    public WorkerImpl(InetSocketAddress address, int mainPort, WorkersManager manager) {
        this.address = address;
        this.mainPort = mainPort;
        this.manager = manager;
    }

    public InetSocketAddress getFullAddress() {
        return address;
    }

    public int getMainPort() {
        return mainPort;
    }

    public Socket getSocket() throws IOException {
        return manager.openSocket(this);
    }

    public WorkersManager getRegisteredManager() {
        return manager;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerImpl worker = (WorkerImpl) o;
        return address.equals(worker.address);

    }

    public int hashCode() {
        return address.hashCode();
    }

    public String toString() {
        return "Worker address: " + address.toString();
    }
}

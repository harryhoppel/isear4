package org.spbgu.pmpu.athynia.central.network.impl;

import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class WorkerImpl implements Worker {
    private final InetSocketAddress address;
    private final WorkersManager manager;

    public WorkerImpl(InetSocketAddress address, WorkersManager manager) {
        this.address = address;
        this.manager = manager;
    }

    public InetSocketAddress getFullAddress() {
        return address;
    }

    public Socket openSocket() throws IOException {
        return manager.openSocket(this);
    }

    public void closeSocket() throws IOException {
        manager.closeSocket(this);
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

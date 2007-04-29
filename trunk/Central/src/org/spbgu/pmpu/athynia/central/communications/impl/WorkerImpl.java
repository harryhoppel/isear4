package org.spbgu.pmpu.athynia.central.communications.impl;

import org.spbgu.pmpu.athynia.central.communications.Worker;
import org.spbgu.pmpu.athynia.central.communications.WorkersManager;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class WorkerImpl implements Worker {
    private final InetSocketAddress address;
    private WorkersManager manager;

    public WorkerImpl(InetSocketAddress address, WorkersManager manager) {
        this.address = address;
        this.manager = manager;
    }

    public boolean isAlive() {
        return manager.isAlive(this);
    }

    public InetSocketAddress getFullAddress() {
        return address;
    }

    public WorkersManager getRegisteredManager() {
        return manager;
    }

    public Socket getSocket() {
        return manager.getSocket(this);
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

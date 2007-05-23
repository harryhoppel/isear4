package org.spbgu.pmpu.athynia.central.network.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.Central;
import org.spbgu.pmpu.athynia.central.classloader.CentralClassLoaderServer;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;
import org.spbgu.pmpu.athynia.common.settings.IllegalConfigException;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: vasiliy
 */
public class BroadcastingDaemon implements Runnable {
    private static final Logger LOG = Logger.getLogger(BroadcastingDaemon.class);

    public static final Set<InetSocketAddress> WORKERS_ADDRESSES = Collections.synchronizedSet(new HashSet<InetSocketAddress>());

    public static final int DEFAULT_REMOTE_BROADCAST_PORT = 44445;
    public static final long DEFAULT_TIMEOUT = 5 * 60 * 1000; //5 minutes

    private final long timeout;
    private final DatagramSocket broadcastSocket;
    private final int broadcastRemotePort;
    private final InetAddress broadcastGroup;
    private final int portToListen;
    private final Runnable workersSignalsGetter;

    public BroadcastingDaemon(String broadcastAddress, int broadcastLocalPort, int portToListen, SocketOpener socketOpener, WorkersManager workersManager) throws IllegalConfigException {
        this(DEFAULT_TIMEOUT, DEFAULT_REMOTE_BROADCAST_PORT, broadcastAddress, broadcastLocalPort, portToListen, socketOpener, workersManager);
    }

    public BroadcastingDaemon(long timeout, String broadcastAddress, int broadcastLocalPort, int portToListen, SocketOpener socketOpener, WorkersManager workersManager) throws IllegalConfigException {
        this(timeout, DEFAULT_REMOTE_BROADCAST_PORT, broadcastAddress, broadcastLocalPort, portToListen, socketOpener, workersManager);
    }

    public BroadcastingDaemon(long timeout, int broadcastRemotePort, String broadcastAddress, int broadcastLocalPort, int portToListen, SocketOpener socketOpener, WorkersManager workersManager) throws IllegalConfigException {
        this.timeout = timeout;
        this.broadcastRemotePort = broadcastRemotePort;
        this.portToListen = portToListen;
        try {
            broadcastSocket = socketOpener.openDatagramSocketOnDefaultNic(broadcastLocalPort);
        } catch (IOException e) {
            throw new IllegalConfigException("Can't open broadcast socket on default nic", e);
        }
        try {
            broadcastGroup = InetAddress.getByName(broadcastAddress);
        } catch (UnknownHostException e) {
            broadcastSocket.close();
            throw new IllegalConfigException("Unknown host: " + broadcastAddress, e);
        }
        try {
            this.workersSignalsGetter = new WorkersSignalGetter(portToListen, socketOpener, workersManager);
        } catch (IOException e) {
            throw new IllegalConfigException("Can't create listener for workers' signals", e);
        }
    }

    public void run() {
        LOG.info("Config accepted, broadcasting started...");
        long previousTime = System.currentTimeMillis();
        final Thread workersSignalsGetterThread = new Thread(Thread.currentThread().getThreadGroup(), workersSignalsGetter, "Workers signals listener/getter");
        workersSignalsGetterThread.setDaemon(true);
        workersSignalsGetterThread.start();
        final byte[] bufferToSend = (portToListen + "," + Central.SERVER_PORT + "," + CentralClassLoaderServer.SERVER_PORT).getBytes();
        while (true) {
            DatagramPacket packet = new DatagramPacket(bufferToSend, 0, bufferToSend.length, broadcastGroup, broadcastRemotePort);
            try {
                broadcastSocket.send(packet);
                LOG.debug("Broadcast packet was sent...");
            } catch (IOException e) {
                LOG.warn("Can't send broadcast message", e);
            }
            while (System.currentTimeMillis() - previousTime < timeout) {
                try {
                    synchronized (this) {
                        wait(timeout);
                    }
                } catch (InterruptedException ignore) {
                }
            }
            previousTime = System.currentTimeMillis();
        }
    }
}

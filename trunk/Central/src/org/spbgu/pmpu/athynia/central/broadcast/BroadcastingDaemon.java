package org.spbgu.pmpu.athynia.central.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.central.Central;
import org.spbgu.pmpu.athynia.central.communications.WorkersManager;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * User: vasiliy
 */
public class BroadcastingDaemon implements Runnable {
    private static final Logger LOG = Logger.getLogger(BroadcastingDaemon.class);

    public static final Set<InetSocketAddress> WORKERS_ADDRESSES = Collections.synchronizedSet(new HashSet<InetSocketAddress>());

    public static final int DEFAULT_BROADCAST_PORT = 44445;
    public static final long DEFAULT_TIMEOUT = 5 * 60 * 1000; //5 minutes

    private final long timeout;
    private final DatagramSocket broadcastSocket;
    private final int broadcastPort;
    private final InetAddress broadcastGroup;
    private final int portToListen;
    private final Runnable workersSignalsGetter;

    public BroadcastingDaemon(String broadcastAddress, int portToListen, String nicName, String nicParticularAddress, WorkersManager workersManager) throws IllegalConfigException {
        this(DEFAULT_TIMEOUT, DEFAULT_BROADCAST_PORT, broadcastAddress, portToListen, nicName, nicParticularAddress, workersManager);
    }

    public BroadcastingDaemon(long timeout, String broadcastAddress, int portToListen, String nicName, String nicParticularAddress, WorkersManager workersManager) throws IllegalConfigException {
        this(timeout, DEFAULT_BROADCAST_PORT, broadcastAddress, portToListen, nicName, nicParticularAddress, workersManager);
    }

    public BroadcastingDaemon(long timeout, int broadcastPort, String broadcastAddress, int portToListen, String nicName, String nicParticularAddress, WorkersManager workersManager) throws IllegalConfigException {
        this.timeout = timeout;
        this.broadcastPort = broadcastPort;
        this.portToListen = portToListen;
        final NetworkInterface broadcastingNic;
        try {
            broadcastingNic = NetworkInterface.getByName(nicName);
            if (broadcastingNic == null) {
                throw new SocketException("No such interface: " + nicName);
            }
        } catch (SocketException e) {
            throw new IllegalConfigException("Problems with network interface(I/O): " + nicName, e);
        }
        InetAddress broadcastInetAddress = null;
        for (Enumeration<InetAddress> broadcastNicAddresses = broadcastingNic.getInetAddresses(); broadcastNicAddresses.hasMoreElements();) {
            InetAddress nextBroadcastAddress = broadcastNicAddresses.nextElement();
            if (nextBroadcastAddress.toString().equals(nicParticularAddress)) {
                broadcastInetAddress = nextBroadcastAddress;
                break;
            }
        }
        if (broadcastInetAddress == null) {
            throw new IllegalConfigException("No such address: " + nicParticularAddress);
        }
        try {
            broadcastSocket = new DatagramSocket(new InetSocketAddress(broadcastInetAddress, broadcastPort));
        } catch (SocketException e) {
            throw new IllegalConfigException("", e);
        }
        try {
            broadcastGroup = InetAddress.getByName(broadcastAddress);
        } catch (UnknownHostException e) {
            broadcastSocket.close();
            throw new IllegalConfigException("Unknown host: " + broadcastAddress, e);
        }
        try {
            this.workersSignalsGetter = new WorkersSignalGetter(portToListen, broadcastInetAddress, workersManager);
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
        final byte[] bufferToSend = (portToListen + "," + Central.SERVER_PORT).getBytes();
        while (true) {
            DatagramPacket packet = new DatagramPacket(bufferToSend, 0, bufferToSend.length, broadcastGroup, broadcastPort);
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
                } catch (InterruptedException e) {
                    broadcastSocket.close();
                    return;  // thread was terminated
                }
            }
            previousTime = System.currentTimeMillis();
        }
    }
}

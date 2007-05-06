package org.spbgu.pmpu.athynia.worker.broadcast;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.worker.settings.IllegalConfigException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * User: vasiliy
 */
public class BroadcastListeningDaemon implements Runnable {
    private static final Logger LOG = Logger.getLogger(BroadcastListeningDaemon.class);

    public static Integer centralBroadcastPort;
    public static Integer centralMainPort;
    public static InetAddress centralAddress;
    public static Integer centralClassLoaderPort;
    public static final Object CENTRAL_ADDRESS_NOTIFICATOR = new Object();

    private final int workerMainPort;
    private final MulticastSocket multicastSocket;
    private final InetAddress inetGroupAddressToJoin;

    public static boolean centralAddressFound = false;

    public BroadcastListeningDaemon(int workerMainPort, int broadcastingPort, String groupAddressToJoin) throws IllegalConfigException {
        this.workerMainPort = workerMainPort;
        try {
            this.inetGroupAddressToJoin = InetAddress.getByName(groupAddressToJoin);
        } catch (UnknownHostException e) {
            throw new IllegalConfigException("Unknown group address(was specified to join it): " + groupAddressToJoin, e);
        }
        try {
            multicastSocket = new MulticastSocket(new InetSocketAddress(broadcastingPort));
            multicastSocket.joinGroup(this.inetGroupAddressToJoin);
        } catch (IOException e) {
            throw new IllegalConfigException("Illegal config parameters: port " + broadcastingPort + ", address " + groupAddressToJoin, e);
        }
//        Runnable mainPortListener = new MainPortListener(workerMainPort);
//        Thread mainPortListenerThread = new Thread(Thread.currentThread().getThreadGroup(), mainPortListener, "Worker main port listener");
//        mainPortListenerThread.setDaemon(true);
//        mainPortListenerThread.start();
    }

    public void run() {
        LOG.info("Config accepted, started listening to broadcast messages");
//        boolean centralAddressFound = false;
        while (!centralAddressFound) {
            DatagramPacket receivedPacket = new DatagramPacket(new byte[256], 0, 256); // 256 bytes is enough to hold central's InetAddress - I hope
            try {
                LOG.debug("Waiting for incoming broadcast message...");
                multicastSocket.receive(receivedPacket);
                LOG.debug("Received broadcast message");
                String data = new String(receivedPacket.getData());
//                TODO
                parseData(data);
//                centralBroadcastPort = veryDumbMethodToParseUnknownIntegers(data);
                centralAddress = receivedPacket.getAddress();
                synchronized (CENTRAL_ADDRESS_NOTIFICATOR) {
                    CENTRAL_ADDRESS_NOTIFICATOR.notifyAll();
                }
                centralAddressFound = true;
                LOG.info("Central found: " + centralAddress.toString() + ":" + centralBroadcastPort + ";" + centralMainPort + ";" + centralClassLoaderPort);
            } catch (IOException e) {
                LOG.error("Error while retrieving central address", e);
            } finally {
                try {
                    multicastSocket.leaveGroup(inetGroupAddressToJoin);
                } catch (IOException e) {
                    LOG.warn("Can't leave multicast group :-(", e);
                }
                multicastSocket.close();
                LOG.debug("Multicast socket closed in worker");
            }
        }
        Socket socket = null;
        OutputStream outputToCentral = null;
        try {
            socket = new Socket(centralAddress, centralBroadcastPort);
            outputToCentral = socket.getOutputStream();
            outputToCentral.write(Integer.toString(workerMainPort).getBytes("US-ASCII"));
        } catch (IOException e) {
            LOG.error("Can't communicate with central", e);
        } finally {
            if (outputToCentral != null) {
                try {
                    outputToCentral.close();
                } catch (IOException e) {
                    LOG.error("Can't close opened stream to central", e);
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("Can't close opened socket to central", e);
                }
            }
        }
    }

    private void parseData(String data) {
        int currentPos = 0;
        String tmp = "";
        if (centralBroadcastPort == null) {
            for (char ch : data.toCharArray()) {
                if (Character.isDigit(ch)) {
                    tmp += ch;
                }
                if (ch == ',') {
                    currentPos = data.indexOf(",");
                    centralBroadcastPort = Integer.parseInt(tmp);
                    tmp = "";
                    break;
                }
            }
        }
        if (centralMainPort == null) {
            char[] chars = data.toCharArray();
            for (int i = currentPos + 1; i < chars.length; i++) {
                if (Character.isDigit(chars[i])) {
                    tmp += chars[i];
                } else if (chars[i] == ',') {
                    currentPos = i;
                    break;
                }
            }
            centralMainPort = Integer.parseInt(tmp);
            tmp = "";
        }
        if (centralClassLoaderPort == null) {
            char[] chars = data.toCharArray();
            for (int i = currentPos; i < chars.length; i++) {
                if (Character.isDigit(chars[i])) {
                    tmp += chars[i];
                }
            }
            centralClassLoaderPort = Integer.parseInt(tmp);
        }
    }
}

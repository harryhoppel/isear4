package org.spbgu.pmpu.athynia.worker.network.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;
import org.spbgu.pmpu.athynia.common.network.impl.SocketOpenerImpl;
import org.spbgu.pmpu.athynia.common.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.worker.network.CentralConnectionManager;
import org.spbgu.pmpu.athynia.worker.network.broadcast.BroadcastListeningDaemon;
import org.spbgu.pmpu.athynia.worker.network.broadcast.MainPortListener;

import java.io.IOException;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class CentralConnectionManagerImpl implements CentralConnectionManager {
    private static final Logger LOG = Logger.getLogger(CentralConnectionManagerImpl.class);

    private volatile Socket socketToCentral;

    private MainPortListener mainPortListener;

    public void start(Settings settings, int mainWorkerClassloaderPort, int mainWorkerPort) {
        int broadcastingPort = settings.getIntValue("broadcast-port");
        String groupAddressToJoin = settings.getValue("group-address-to-join");
        String defaultLocalNic = settings.getValue("default-local-nic");
        String defaultLocalAddress = settings.getValue("default-local-address");
        try {
            SocketOpener socketOpener = new SocketOpenerImpl(defaultLocalNic, defaultLocalAddress);
            ThreadGroup broadcastingThreadGroup = new ThreadGroup("Broadcasting threads");
            BroadcastListeningDaemon broadcastListeningDaemon = new BroadcastListeningDaemon(mainWorkerClassloaderPort, mainWorkerPort, broadcastingPort, groupAddressToJoin);
            Thread broadcastListeningDaemonThread = new Thread(broadcastingThreadGroup, broadcastListeningDaemon, "Broadcast listening thread");
            broadcastListeningDaemonThread.setDaemon(true);
            broadcastListeningDaemonThread.start();
            mainPortListener = new MainPortListener(mainWorkerPort, socketOpener);
            Thread mainPortListenerThread = new Thread(broadcastingThreadGroup, mainPortListener, "Worker main port listener");
            mainPortListenerThread.setDaemon(true);
            mainPortListenerThread.start();
        } catch (IllegalConfigException e) {
            LOG.error("Can't listen to central's broadcasts!", e);
        } catch (IOException e) {
            LOG.error("Can't setup socket opener", e);
        }
    }

    public synchronized Socket getSocket() throws IOException {
        synchronized (mainPortListener) {
            if (socketToCentral != null && !socketToCentral.isClosed()
                    && socketToCentral.isBound() && socketToCentral.isConnected()
                    && !socketToCentral.isInputShutdown() && !socketToCentral.isOutputShutdown()) {
                try {
                    socketToCentral.getInputStream();
                    socketToCentral.getOutputStream();
                } catch (IOException e) {
                    socketToCentral = mainPortListener.getCentralConnection();
                }
            } else {
                socketToCentral = mainPortListener.getCentralConnection();
            }
        }
        return socketToCentral;
    }

    public synchronized void closeSocket() {
        synchronized (mainPortListener) {
            mainPortListener.closeCentralConnection();
        }
        socketToCentral = null;
    }

}

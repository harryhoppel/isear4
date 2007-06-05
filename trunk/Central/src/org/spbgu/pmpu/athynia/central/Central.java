package org.spbgu.pmpu.athynia.central;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.classloader.CentralClassLoaderServer;
import org.spbgu.pmpu.athynia.central.network.NetworkRunner;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.broadcast.BroadcastingDaemon;
import org.spbgu.pmpu.athynia.central.network.communications.CentralMainPortListener;
import org.spbgu.pmpu.athynia.central.network.communications.split.SplitReceiver;
import org.spbgu.pmpu.athynia.central.network.communications.split.impl.DataSplitterImpl;
import org.spbgu.pmpu.athynia.central.network.impl.DataImpl;
import org.spbgu.pmpu.athynia.central.network.impl.DataJoinerImpl;
import org.spbgu.pmpu.athynia.central.network.impl.NetworkRunnerImpl;
import org.spbgu.pmpu.athynia.common.network.SocketOpener;
import org.spbgu.pmpu.athynia.common.network.impl.SocketOpenerImpl;
import org.spbgu.pmpu.athynia.common.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.common.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Author: Selivanov
 * Date: 09.02.2007
 * Time: 20:22:08
 */
public class Central {
    private static final Logger LOG = Logger.getLogger(Central.class);

    static final Settings settings = DataManager.getInstance().getData(Settings.class).childSettings("common");
    static final Settings centralSettings = settings.childSettings("central-server");
    final Settings broadcastSettings = settings.childSettings("broadcast");

    public static final int SERVER_PORT = Integer.parseInt(centralSettings.getValue("port"));
    public InetAddress SERVER_INETADDRESS;

    private final WorkersManager workersManager;
    private final String classLoaderHomeDir;

    private SocketOpener socketOpener;

    public Central() throws MalformedURLException {
        workersManager = DataManager.getInstance().getData(WorkersManager.class);
        classLoaderHomeDir = centralSettings.getValue("class-loader-home-dir");
        try {
            SERVER_INETADDRESS = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        setUpSocketOpener();
        setUpWorkersManager();
        startBroadcasting();
        startCentralMainPortListener();
        startClassLoaderCentralPart();
    }

    private void setUpWorkersManager() {
        WorkersManager workersManager = DataManager.getInstance().getData(WorkersManager.class);
        workersManager.setupWorkerConnectionsManager(socketOpener);
    }

    private void setUpSocketOpener() {
        String nicName = broadcastSettings.getValue("nicName");
        String nicParticularAddress = broadcastSettings.getValue("nicParticularAddress");
        try {
            socketOpener = new SocketOpenerImpl(nicName, nicParticularAddress);
        } catch (IOException e) {
            LOG.error("Can't setup socket opener", e);
        }
    }

    private void startBroadcasting() {
        //start broadcasting daemon
        String broadcastAddress = broadcastSettings.getValue("broadcastAddress");
        int broadcastLocalPort = broadcastSettings.getIntValue("broadcast-local-port");
        int portToListen = broadcastSettings.getIntValue("portToListen");
        int broadcastingTimeout = broadcastSettings.getIntValue("broadcasting-timeout-in-ms");
        try {
            ThreadGroup broadcastingThreadGroup = new ThreadGroup("Broadcasting threads");
            broadcastingThreadGroup.setMaxPriority(Thread.MIN_PRIORITY);
            broadcastingThreadGroup.setDaemon(true);
            BroadcastingDaemon broadcastingDaemon = new BroadcastingDaemon(broadcastingTimeout, broadcastAddress, broadcastLocalPort, portToListen, socketOpener, workersManager);
            Thread broadcastingDaemonThread = new Thread(broadcastingThreadGroup, broadcastingDaemon, "Broadcasting daemon");
            broadcastingDaemonThread.start();
        } catch (IllegalConfigException e) {
            LOG.error("Illegal broadcasting config: can't search for workers", e);
        }
    }

    private void startCentralMainPortListener() {
        try {
            CentralMainPortListener centralMainPortListener = new CentralMainPortListener(SERVER_PORT, workersManager);
            Thread centralMainPortListenerThread = new Thread(centralMainPortListener, "Central main port listener");
            centralMainPortListenerThread.setDaemon(true);
            centralMainPortListenerThread.start();
        } catch (IllegalConfigException e) {
            LOG.fatal("Can't create central main port listening thread", e);
        }
    }

    private void startClassLoaderCentralPart() {
        try {
            File homeDirectory = new File(classLoaderHomeDir);
            CentralClassLoaderServer centralClassLoaderServer = new CentralClassLoaderServer(homeDirectory);
            centralClassLoaderServer.startServer();
        } catch (MalformedURLException e) {
            LOG.fatal("Can't start class loader on central", e);
        }
    }

    public static void main(String[] args) throws Exception {
        Central central = new Central();
        central.start();
        Thread.sleep(10 * 1000);
        LOG.info("Start sending the code");
        NetworkRunner networkRunner = new NetworkRunnerImpl();
        String joined = networkRunner.runRemotely(SplitReceiver.class, 
                new DataImpl("xxx", "Hello, world!"), new DataSplitterImpl(),
                new DataImpl("xxx", null), new DataJoinerImpl());
        System.out.println("joined = " + joined);
    }
}

package org.spbgu.pmpu.athynia.central;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.broadcast.BroadcastingDaemon;
import org.spbgu.pmpu.athynia.central.classloader.CentralClassLoaderServer;
import org.spbgu.pmpu.athynia.central.communications.WorkersManager;
import org.spbgu.pmpu.athynia.central.communications.CentralMainPortListener;
import org.spbgu.pmpu.athynia.central.communications.WorkersExecutorSender;
import org.spbgu.pmpu.athynia.central.communications.Worker;
import org.spbgu.pmpu.athynia.central.communications.impl.WorkersExecutorSenderImpl;
import org.spbgu.pmpu.athynia.central.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.central.settings.Settings;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Set;

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
        startBroadcasting();
        startCentralMainPortListener();
        startClassLoaderCentralPart();
    }

    private void startBroadcasting() {
        //start broadcasting daemon
        String broadcastAddress = broadcastSettings.getValue("broadcastAddress");
        int portToListen = broadcastSettings.getIntValue("portToListen");
        String nicName = broadcastSettings.getValue("nicName");
        int broadcastingTimeout = broadcastSettings.getIntValue("broadcasting-timeout-in-ms");
        String nicParticularAddress = broadcastSettings.getValue("nicParticularAddress");
        try {
            ThreadGroup broadcastingThreadGroup = new ThreadGroup("Broadcasting threads");
            broadcastingThreadGroup.setMaxPriority(Thread.MIN_PRIORITY);
            broadcastingThreadGroup.setDaemon(true);
            BroadcastingDaemon broadcastingDaemon = new BroadcastingDaemon(broadcastingTimeout, broadcastAddress, portToListen, nicName, nicParticularAddress, workersManager);
            Thread broadcastingDaemonThread = new Thread(broadcastingThreadGroup, broadcastingDaemon, "Broadcast daemon");
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
        Thread.sleep(10*1000);
        LOG.info("Start sending the code");
        WorkersExecutorSender workersExecutorSender = new WorkersExecutorSenderImpl();
        Set<Worker> workers = DataManager.getInstance().getData(WorkersManager.class).getAll();
        for (Worker worker : workers) {
            workersExecutorSender.runExecutorOnWorker(worker, "org.spbgu.pmpu.athynia.central.communications.split.SplitReceiver");
        }
    }
}

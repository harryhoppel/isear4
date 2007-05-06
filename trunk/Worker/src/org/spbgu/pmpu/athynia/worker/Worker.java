package org.spbgu.pmpu.athynia.worker;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.spbgu.pmpu.athynia.worker.broadcast.BroadcastListeningDaemon;
import org.spbgu.pmpu.athynia.worker.network.Processor;
import org.spbgu.pmpu.athynia.worker.network.Server;
import org.spbgu.pmpu.athynia.worker.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.worker.settings.Settings;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.concurrent.Executors;

/**
 * Author: Selivanov
 * Date: 13.04.2007
 * Time: 0:54:54
 */
public class Worker {
    private static final Logger LOG = Logger.getLogger(Worker.class);

    final Settings settings = DataManager.getInstance().getData(Settings.class).childSettings("common");
    final Settings workerSettings = settings.childSettings("worker-server");
    final Settings broadcastSettings = DataManager.getInstance().getData(Settings.class).childSettings("broadcast");
    int SERVER_PORT = Integer.parseInt(workerSettings.getValue("port"));
    private final int mainWorkerPort = DataManager.getInstance().getData(Settings.class).getIntValue("worker-main-port");

    java.util.concurrent.Executor executor;

    public Worker() throws MalformedURLException {
        PropertyConfigurator.configure("log4j.properties");
        executor = Executors.newFixedThreadPool(4);
    }

    public void start() throws IOException, IllegalConfigException, InterruptedException {
        startBroadcast();
        synchronized (BroadcastListeningDaemon.CENTRAL_ADDRESS_NOTIFICATOR) {
            BroadcastListeningDaemon.CENTRAL_ADDRESS_NOTIFICATOR.wait();
        }
//        while (!BroadcastListeningDaemon.centralAddressFound);
        startServer();
    }

    private void startBroadcast() {
        int broadcastingPort = broadcastSettings.getIntValue("broadcast-port");
        String groupAddressToJoin = broadcastSettings.getValue("group-address-to-join");
        try {
            BroadcastListeningDaemon broadcastListeningDaemon = new BroadcastListeningDaemon(mainWorkerPort, broadcastingPort, groupAddressToJoin);
            Thread broadcastListeningDaemonThread = new Thread(broadcastListeningDaemon, "Broadcast listening thread");
            broadcastListeningDaemonThread.setDaemon(true);
            broadcastListeningDaemonThread.start();
        } catch (IllegalConfigException e) {
            LOG.error("Can't listen to central's broadcasts!", e);
        }
    }

    private void startServer() throws IOException {
        Processor processor = new Processor();
        executor.execute(processor);
        executor.execute(new Server(InetAddress.getLocalHost(), mainWorkerPort, processor));
    }

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker();
        worker.start();
    }
}

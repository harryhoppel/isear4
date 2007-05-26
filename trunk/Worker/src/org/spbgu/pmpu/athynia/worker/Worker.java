package org.spbgu.pmpu.athynia.worker;

import org.apache.log4j.PropertyConfigurator;
import org.spbgu.pmpu.athynia.common.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.worker.network.CentralConnectionManager;
import org.spbgu.pmpu.athynia.worker.network.Processor;
import org.spbgu.pmpu.athynia.worker.network.Server;
import org.spbgu.pmpu.athynia.worker.network.broadcast.BroadcastListeningDaemon;

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
    public static CentralConnectionManager centralConnectionManager;

    private final Settings broadcastSettings = DataManager.getInstance().getData(Settings.class).childSettings("broadcast");
    private final Settings serverSettings = DataManager.getInstance().getData(Settings.class).childSettings("server");
    private final String HOST_ADDRESS = serverSettings.getValue("host-address");
    private final int MAIN_WORKER_CLASSOADER_PORT = serverSettings.getIntValue("worker-main-classloader-port");
    private final int MAIN_WORKER_PORT = serverSettings.getIntValue("worker-main-port");

    private final java.util.concurrent.Executor executor;

    public Worker() throws MalformedURLException {
        PropertyConfigurator.configure("log4j.properties");
        executor = Executors.newFixedThreadPool(4);
    }

    public void start() throws IOException, IllegalConfigException, InterruptedException {
        centralConnectionManager = DataManager.getInstance().getData(CentralConnectionManager.class);
        centralConnectionManager.start(broadcastSettings, MAIN_WORKER_CLASSOADER_PORT, MAIN_WORKER_PORT);
        synchronized (BroadcastListeningDaemon.CENTRAL_ADDRESS_NOTIFICATOR) {
            BroadcastListeningDaemon.CENTRAL_ADDRESS_NOTIFICATOR.wait();
        }
        startServer();
    }

    private void startServer() throws IOException {
        Processor processor = new Processor();
        executor.execute(processor);
        executor.execute(new Server(InetAddress.getByName(HOST_ADDRESS), MAIN_WORKER_CLASSOADER_PORT, processor));
    }

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker();
        worker.start();
    }
}

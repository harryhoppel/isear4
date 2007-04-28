package org.spbgu.pmpu.athynia.central;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.spbgu.pmpu.athynia.central.broadcast.BroadcastingDaemon;
import org.spbgu.pmpu.athynia.central.classloader.ZipClassReader;
import org.spbgu.pmpu.athynia.central.network.Client;
import org.spbgu.pmpu.athynia.central.network.Processor;
import org.spbgu.pmpu.athynia.central.network.Server;
import org.spbgu.pmpu.athynia.central.settings.IllegalConfigException;
import org.spbgu.pmpu.athynia.central.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    private File homeDirectory;
    private ZipClassReader classReader;
    private Executor executor;

    public Central() throws MalformedURLException {
        PropertyConfigurator.configure("log4j.properties");
        homeDirectory = new File(centralSettings.getValue("server-directory"));
        classReader = new ZipClassReader(homeDirectory);
        executor = Executors.newFixedThreadPool(4);
        if (homeDirectory.exists() && homeDirectory.isFile()) {
            scanHomeDirectory();
        }
        try {
            SERVER_INETADDRESS = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void executeClass(InetAddress inetAddress, int port, String className) {
        System.out.println("CENTRAL:executeClass:" + className);
        send(inetAddress, port, (className).getBytes());

    }

    public byte[] getClassAsByteArray(String className) {
        return classReader.getClassFromCache(className);
    }

    public void split() {
    }

    public void join() {
    }

    public void  start() {
       startServer();
        startBroadcasting();
    }

    public void startServer() {
        try {
            Processor processor = new Processor();
            executor.execute(processor);
            executor.execute(new Server(InetAddress.getLocalHost(), SERVER_PORT, processor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startBroadcasting() {
        //start broadcasting daemon
        String broadcastAddress = broadcastSettings.getValue("broadcastAddress");
        int portToListen = broadcastSettings.getIntValue("portToListen");
        String nicName = broadcastSettings.getValue("nicName");
        int broadcastingTimeout = broadcastSettings.getIntValue("broadcasting-timeout-in-ms");
        String nicParticularAddress = broadcastSettings.getValue("nicParticularAddress");
        try {
            BroadcastingDaemon broadcastingDaemon = new BroadcastingDaemon(broadcastingTimeout, broadcastAddress, portToListen, nicName, nicParticularAddress);
            Thread broadcastingDaemonThread = new Thread(new ThreadGroup("Broadcasting threads"), broadcastingDaemon, "Broadcast daemon");
            broadcastingDaemonThread.setDaemon(true);
            broadcastingDaemonThread.start();
        } catch (IllegalConfigException e) {
            LOG.error("Illegal broadcasting config: can't search for workers", e);
        }
    }

    private void send(InetAddress inetAddress, int port, byte[] sendBytes) {
        try {
            Client client = new Client(inetAddress, port);
            Thread clientThread = new Thread(client);
            clientThread.setDaemon(true);
            clientThread.start();
            client.send(sendBytes, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scanHomeDirectory() {
        File[] files = homeDirectory.listFiles();
        for (File file : files) {
            if (file.toURI().toString().endsWith(".zip") || file.toURI().toString().endsWith(".jar")) {
                classReader.readZipFile(file);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Central central = new Central();
        central.start();
        central.executeClass(InetAddress.getLocalHost(), 8181, "org.spbgu.pmpu.athynia.central.classloader.usercode.simple.SimpleExecutor");
    }


}

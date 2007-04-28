package org.spbgu.pmpu.athynia.worker.classloader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.spbgu.pmpu.athynia.worker.DataManager;
import org.spbgu.pmpu.athynia.worker.broadcast.BroadcastListeningDaemon;
import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;
import org.spbgu.pmpu.athynia.worker.settings.Settings;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * User: Selivanov
 * Date: 20.04.2007
 */
public class NetworkClassExecutor implements ClassExecutor {
    private static final Logger LOG = Logger.getLogger(NetworkClassExecutor.class);
    final Settings settings = DataManager.getInstance().getData(Settings.class).childSettings("classloader");
    private NetworkClassLoader classLoader;
    private InetAddress centralAddress;
    private Integer cenralPort;

    static {
        PropertyConfigurator.configure("log4j.properties");
    }

    public NetworkClassExecutor() {
        this(BroadcastListeningDaemon.centralAddress, BroadcastListeningDaemon.centralMainPort);
    }

    public NetworkClassExecutor(InetAddress centralAddress, int cenralPort) {
        this.centralAddress = centralAddress;
        this.cenralPort = cenralPort;
        try {
            File homeDirectory = new File(settings.getValue("home-directory"));
            if (homeDirectory.exists())
                classLoader = new NetworkClassLoader(new File(settings.getValue("home-directory")));
            else
                LOG.info("home directory: " + homeDirectory.toURI().toString() + " not exists!!");
                classLoader = new NetworkClassLoader(new File(""));
        } catch (MalformedURLException e) {
            LOG.warn("Unknown home directory", e);
        }
    }

    public NetworkClassExecutor(File homeDirectory, InetAddress centralAddress, Integer cenralPort) {
        this.centralAddress = centralAddress;
        this.cenralPort = cenralPort;
        try {
            if (homeDirectory.exists())
                classLoader = new NetworkClassLoader(homeDirectory);
            else
                LOG.info("home directory: " + homeDirectory.toURI().toString() + " not exists!!");
        } catch (MalformedURLException e) {
            LOG.warn("Unknown home directory", e);
        }
    }

    public boolean executeClass(String className) {
        try {
            SocketAddress socketAddress = new InetSocketAddress(centralAddress, cenralPort);
            Socket socket = new Socket();
            int timeoutMs = 2000;   // 2 seconds
            socket.connect(socketAddress, timeoutMs);
            //if ClassCastException => return false todo: unload this class
            Executor executor = (Executor) classLoader.loadClass(className).newInstance();
            executor.execute(socket.getInputStream(), socket.getOutputStream(), new LocalResourceManager());
        } catch (Throwable t) {
            //todo LOG excetion
            LOG.warn("", t);
            return false;
        }
        //todo auto close streams
        return true;
    }
}

package org.spbgu.pmpu.athynia.worker.classloader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.spbgu.pmpu.athynia.worker.DataManager;
import org.spbgu.pmpu.athynia.worker.network.CentralConnectionManager;
import org.spbgu.pmpu.athynia.worker.network.broadcast.BroadcastListeningDaemon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;

/**
 * User: Selivanov
 * Date: 20.04.2007
 */
public class NetworkClassExecutor implements ClassExecutor {
    private static final Logger LOG = Logger.getLogger(NetworkClassExecutor.class);
    private static final Settings settings = DataManager.getInstance().getData(Settings.class).childSettings("classloader");

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
        this(new File(settings.getValue("home-directory")), centralAddress, cenralPort);
    }

    public NetworkClassExecutor(File homeDirectory, InetAddress centralAddress, Integer cenralPort) {
        this.centralAddress = centralAddress;
        this.cenralPort = cenralPort;
        try {
            if (homeDirectory.exists()) {
                classLoader = new NetworkClassLoader(homeDirectory);
            } else {
                LOG.info("home directory: " + homeDirectory.toURI().toString() + " not exists!!");
                classLoader = new NetworkClassLoader(new File(""));
            }
        } catch (MalformedURLException e) {
            LOG.warn("Unknown home directory", e);
        }
    }

    public boolean executeClass(String className) {
        OutputStream output = null;
        InputStream input = null;
        try {
            Executor executor = (Executor) classLoader.loadClass(className).newInstance();
            Socket socket = DataManager.getInstance().getData(CentralConnectionManager.class).getSocket();
            ResourceManager resourceManager = DataManager.getInstance().getData(ResourceManager.class);
            LOG.debug("Socket to central is bound to address: " + socket.getInetAddress() + ":" + socket.getPort());
            input = socket.getInputStream();
            output = socket.getOutputStream();
            executor.execute(input, output, resourceManager);
        } catch (Throwable t) {
            LOG.warn("Exception was thrown while executing class:" + className, t);
            return false;
        } finally{
            try {
                output.flush();
            } catch (IOException e) {
            }
        }
        return true;
    }
}

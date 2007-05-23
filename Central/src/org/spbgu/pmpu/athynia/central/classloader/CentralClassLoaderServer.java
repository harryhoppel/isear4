package org.spbgu.pmpu.athynia.central.classloader;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.DataManager;
import org.spbgu.pmpu.athynia.central.classloader.network.Processor;
import org.spbgu.pmpu.athynia.central.classloader.network.Server;
import org.spbgu.pmpu.athynia.common.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: Selivanov
 * Date: 27.04.2007
 */
public class CentralClassLoaderServer {
    private static final Logger LOG = Logger.getLogger(CentralClassLoaderServer.class);
    private static final Settings CLASS_LOADER_SETTINGS = DataManager.getInstance().getData(Settings.class).childSettings("common").childSettings("classloader");
    private static final String HOST_ADDRESS = CLASS_LOADER_SETTINGS.getValue("host-address");
    public static final int SERVER_PORT = CLASS_LOADER_SETTINGS.getIntValue("port");

    private Executor executor;
    ZipClassReader classReader;

    public CentralClassLoaderServer(File homeDirectory) throws MalformedURLException {
        classReader = new ZipClassReader();
        executor = Executors.newFixedThreadPool(4);
        scanHomeDirectory(homeDirectory);
    }

    private void scanHomeDirectory(File homedirectory) throws MalformedURLException {
        File[] files = homedirectory.listFiles();
        if (files == null) {
            LOG.error("Can't find classloader home directory: " + homedirectory);
            return;
        }
        if (files.length > 0) {
            for (File file : files) {
                if (file.toURI().toURL().toString().endsWith(".zip")) {
                    LOG.debug("Reading class loader classes from zip-file: " + file.getAbsolutePath());
                    classReader.readZipFile(file);
                }
            }
        }
    }

    public void startServer() {
        try {
            Processor processor = new Processor(classReader);
            executor.execute(processor);
            executor.execute(new Server(InetAddress.getByName(HOST_ADDRESS), SERVER_PORT, processor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

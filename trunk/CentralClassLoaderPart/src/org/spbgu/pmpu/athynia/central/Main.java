package org.spbgu.pmpu.athynia.central;

import org.spbgu.pmpu.athynia.central.classloader.ZipClassReader;
import org.spbgu.pmpu.athynia.central.network.Processor;
import org.spbgu.pmpu.athynia.central.network.Server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: Selivanov
 * Date: 27.04.2007
 */
public class Main {
    private Executor executor;
    private static final int SERVER_PORT = 10000;
    ZipClassReader classReader;

    public Main(File homeDirectory) throws MalformedURLException {
        classReader = new ZipClassReader(homeDirectory);
        executor = Executors.newFixedThreadPool(4);
        scanHomeDirectory(homeDirectory);
    }

    public static void main(String[] args) throws IOException {
      File homeDirectory = new File(args[0]);
      Main main = new Main(homeDirectory);
      main.startServer();
    }

    private void scanHomeDirectory(File homedirectory) throws MalformedURLException {
        for (File file : homedirectory.listFiles()) {
            if (file.toURI().toURL().toString().endsWith(".zip")) {
                classReader.readZipFile(file);
            }
        }
    }

    public void startServer() {
        try {
            Processor processor = new Processor(classReader);
            executor.execute(processor);
            executor.execute(new Server(InetAddress.getLocalHost(), SERVER_PORT, processor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

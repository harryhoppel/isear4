package org.spbgu.pmpu.athynia.worker.network;

import org.spbgu.pmpu.athynia.common.settings.Settings;

import java.net.Socket;
import java.io.IOException;

/**
 * User: vasiliy
 */
public interface CentralConnectionManager {
    void start(Settings settings, int mainWorkerClassloaderPort, int mainWorkerPort);

    Socket getSocket() throws IOException;
    void closeSocket();
}

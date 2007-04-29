package org.spbgu.pmpu.athynia.central.communications;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: vasiliy
 */
public interface Worker {
    boolean isAlive();

    InetSocketAddress getFullAddress();

    WorkersManager getRegisteredManager();

    Socket getSocket();
}

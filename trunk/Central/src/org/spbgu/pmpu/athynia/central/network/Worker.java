package org.spbgu.pmpu.athynia.central.network;

import org.spbgu.pmpu.athynia.central.network.WorkersManager;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;

/**
 * User: vasiliy
 */
public interface Worker {
    InetSocketAddress getFullAddress();

    Socket openSocket() throws IOException;
    void closeSocket() throws IOException;
}

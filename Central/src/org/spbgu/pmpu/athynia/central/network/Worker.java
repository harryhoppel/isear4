package org.spbgu.pmpu.athynia.central.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: vasiliy
 */
public interface Worker {
    InetSocketAddress getFullAddress();

    int getMainPort();

    Socket getSocket() throws IOException;
}

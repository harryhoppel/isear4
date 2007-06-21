package org.spbgu.pmpu.athynia.central.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: vasiliy
 */
public interface Worker {
    final Integer NEW_STATE = 0;
    final Integer PROCESSING_STATE = 1;
    final Integer COMPLETE_STATE = 2;
    final Integer FAIL_STATE = 3;

    int getState();
    void setState(int state);

    InetSocketAddress getFullAddress();

    int getMainPort();

    Socket openSocket() throws IOException;
    void closeSocket() throws IOException;
}

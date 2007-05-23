package org.spbgu.pmpu.athynia.common.network;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.DatagramSocket;
import java.io.IOException;

/**
 * User: vasiliy
 */
public interface SocketOpener {
    Socket openSocketOnDefaultNic(String remoteHost, int remotePort) throws IOException;
//    Socket openSocket(String localNic, String localAddress, String remoteHost, int remotePort) throws IOException;

    ServerSocket openServerSocketOnDefaultNic(int bindPort) throws IOException;
//    ServerSocket openServerSocket(String localNic, String localAddress, int bindPort) throws IOException;

    DatagramSocket openDatagramSocketOnDefaultNic(int bindPort) throws IOException;
//    DatagramSocket openDatagramSocket(String localNic, String localAddress, int bindPort) throws IOException;
}

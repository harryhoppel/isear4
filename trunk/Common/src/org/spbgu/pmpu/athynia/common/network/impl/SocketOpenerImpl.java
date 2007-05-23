package org.spbgu.pmpu.athynia.common.network.impl;

import org.spbgu.pmpu.athynia.common.network.SocketOpener;

import java.net.*;
import java.util.Enumeration;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class SocketOpenerImpl implements SocketOpener {
    private String lastNic = "";
    private String lastAddress = "";
    private InetAddress lastInetAddress = null;

    private final InetAddress defaultInetAddress;

    public SocketOpenerImpl(String defaultLocalNic, String defaultLocalAddress) throws IOException {
        defaultInetAddress = detectNicInetAddress(defaultLocalNic, defaultLocalAddress);
    }

    public Socket openSocketOnDefaultNic(String remoteHost, int remotePort) throws IOException {
        return openSocket(defaultInetAddress, remoteHost, remotePort);
    }

    public Socket openSocket(String localNic, String localAddress, String remoteHost, int remotePort) throws IOException {
        InetAddress bindInetAddress = detectNicInetAddress(localNic, localAddress);
        return openSocket(bindInetAddress, remoteHost, remotePort);
    }

    public ServerSocket openServerSocketOnDefaultNic(int port) throws IOException {
        return openServerSocket(defaultInetAddress, port);
    }

    public ServerSocket openServerSocket(String localNic, String localAddress, int port) throws IOException {
        InetAddress bindInetAddress = detectNicInetAddress(localNic, localAddress);
        return openServerSocket(bindInetAddress, port);
    }

    public DatagramSocket openDatagramSocketOnDefaultNic(int bindPort) throws IOException {
        return new DatagramSocket(new InetSocketAddress(defaultInetAddress, bindPort));
    }

    public DatagramSocket openDatagramSocket(String localNic, String localAddress, int bindPort) throws IOException {
        InetAddress bindInetAddress = detectNicInetAddress(localNic, localAddress);
        DatagramSocket ret = new DatagramSocket();
        ret.bind(new InetSocketAddress(bindInetAddress, bindPort));
        return ret;
    }

    private InetAddress detectNicInetAddress(String localNic, String localAddress) throws IOException {
        InetAddress bindInetAddress = null;
        if (lastNic.equals(localNic) && lastAddress.equals(localAddress)) {
            bindInetAddress = lastInetAddress;
        } else  {
            final NetworkInterface broadcastingNic = NetworkInterface.getByName(localNic);
            if (broadcastingNic == null) {
                throw new IOException("No such interface: " + localNic);
            }
            for (Enumeration<InetAddress> broadcastNicAddresses = broadcastingNic.getInetAddresses(); broadcastNicAddresses.hasMoreElements();) {
                InetAddress nextBroadcastAddress = broadcastNicAddresses.nextElement();
                if (nextBroadcastAddress.toString().equals(localAddress)) {
                    bindInetAddress = nextBroadcastAddress;
                    break;
                }
            }
        }
        if (bindInetAddress == null) {
            throw new IOException("No such address: " + localAddress + " on the following network interface: " + localNic);
        }
        lastNic = localNic;
        lastAddress = localAddress;
        lastInetAddress = bindInetAddress;
        return bindInetAddress;
    }

    private Socket openSocket(InetAddress bindTo, String remoteHost, int remotePort) throws IOException {
        Socket ret = new Socket();
        ret.bind(new InetSocketAddress(bindTo, 0));
        ret.connect(new InetSocketAddress(remoteHost, remotePort));
        return ret;
    }

    private ServerSocket openServerSocket(InetAddress bindTo, int port) throws IOException {
        ServerSocket ret = new ServerSocket();
        ret.bind(new InetSocketAddress(bindTo, port));
        return ret;
    }
}

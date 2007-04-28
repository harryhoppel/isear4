package org.spbgu.pmpu.athynia.central.network;

import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * User: Selivanov
 * Date: 14.04.2007
 * Time: 1:49:08
 */
public class Connection {
    Socket socket = null;
    InputStream in = null;
    OutputStream out = null;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    public Connection(String host, int port) throws IOException {
        this(new Socket(InetAddress.getByName(host), port));
    }

    public InputStream getInputStream() {
        return in;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public void setInputStream(InputStream in) {
        this.in = in;
    }

    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {
                System.out.println("Connection: " + e);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    public String toString() {
        return getInetAddress().getHostAddress() + ":" + getPort();
    }

    public void setTimeout(int timeout)
        throws SocketException {
        socket.setSoTimeout(timeout);
    }
}
package org.spbgu.pmpu.athynia.worker.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Author: Selivanov
 * Date: 10.03.2007
 * Time: 1:31:23
 */
public class ServerDataEvent {
    public Server server;
    public SocketChannel socket;
    public byte[] data;
    public Connection connection;

    public ServerDataEvent(Server server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data = data;
        try {
            this.connection = new Connection(socket.socket());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

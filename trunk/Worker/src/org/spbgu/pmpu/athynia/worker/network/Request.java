package org.spbgu.pmpu.athynia.worker.network;

import java.nio.channels.SocketChannel;

/**
 * Author: Selivanov
 * Date: 10.03.2007
 * Time: 1:12:19
 */
public class Request {
    public static final int REGISTER = 1;
    public static final int OP_CHANGE = 2;

    public SocketChannel socket;
    public int type;
    public int ops;

    public Request(SocketChannel socket, int type, int ops) {
        this.socket = socket;
        this.type = type;
        this.ops = ops;
    }
}

package org.spbgu.pmpu.athynia.worker.network;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Client implements Runnable {
    Logger LOG = Logger.getLogger(Client.class);
    private InetAddress hostAddress;
    private int port;

    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(4000);//todo

    private final List<Request> pendingChanges = new LinkedList<Request>();
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
    private Map<SocketChannel, ResponseHandler> rspHandlers = Collections.synchronizedMap(new HashMap<SocketChannel, ResponseHandler>());

    public Client(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        selector = SelectorProvider.provider().openSelector();
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                synchronized (pendingChanges) {
                    for (Request pending : pendingChanges) {
                        switch (pending.type) {
                            case Request.OP_CHANGE:
                                SelectionKey key = pending.socket.keyFor(selector);
                                key.interestOps(pending.ops);
                                break;
                            case Request.REGISTER:
                                pending.socket.register(selector, pending.ops);
                                break;
                        }
                    }
                    pendingChanges.clear();
                }

                selector.select();

                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isConnectable()) {
                        finishConnection(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            } catch (Exception e) {
                LOG.warn("warn conneting to central:", e);
            }
        }
    }

    public void send(byte[] data, ResponseHandler handler) throws IOException {
        SocketChannel socket = SocketChannel.open();
        socket.configureBlocking(false);
        socket.connect(new InetSocketAddress(hostAddress, port));
        synchronized (pendingChanges) {
            pendingChanges.add(new Request(socket, Request.REGISTER, SelectionKey.OP_CONNECT));
        }
        rspHandlers.put(socket, handler);
        synchronized (pendingData) {
            List<ByteBuffer> queue = pendingData.get(socket);
            if (queue == null) {
                queue = new ArrayList<ByteBuffer>();
                pendingData.put(socket, queue);
            }
            queue.add(ByteBuffer.wrap(data));
            pendingData.notify();
        }
        selector.wakeup();
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        readBuffer.clear();
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            key.channel().close();
            key.cancel();
            return;
        }

        handleResponse(socketChannel, readBuffer.array(), numRead);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (pendingData) {
            List<ByteBuffer> queue = pendingData.get(socketChannel);
            if (queue == null) {
                try {
                    pendingData.wait();
                } catch (InterruptedException e) {
                    // interrupt
                }
                queue = pendingData.get(socketChannel);
            }
            while (!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    break;
                }
                queue.remove(0);
            }
            if (queue.isEmpty()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
        byte[] rspData = new byte[numRead];
        System.arraycopy(data, 0, rspData, 0, numRead);

        ResponseHandler handler = rspHandlers.get(socketChannel);

        if (handler.handleResponse(rspData)) {
            socketChannel.close();
            socketChannel.keyFor(selector).cancel();
        }
    }

    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            return;
        }
        key.interestOps(SelectionKey.OP_WRITE);
    }
}

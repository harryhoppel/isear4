package org.spbgu.pmpu.athynia.worker.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: Selivanov
 * Date: 09.02.2007
 * Time: 20:04:49
 */
public class Server implements Runnable {
    private InetAddress hostAddress;
    private int port;
    private Processor processor;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private final List<Request> pendingChanges = new LinkedList<Request>();
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
    private boolean isRunning = true;


    public Server(InetAddress hostAddress, int port, Processor processor) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = initSelector();
        this.processor = processor;
    }

    private Selector initSelector() throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostAddress, port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(inetSocketAddress);
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
        return socketSelector;
    }

    public void send(SocketChannel socket, byte[] data) {
        synchronized (pendingChanges) {
            pendingChanges.add(new Request(socket, Request.OP_CHANGE, SelectionKey.OP_WRITE));

            synchronized (pendingData) {
                List<ByteBuffer> queue = pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList<ByteBuffer>();
                    pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
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
            e.printStackTrace();
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            key.channel().close();
            key.cancel();
            return;
        }

        processor.processData(this, socketChannel, readBuffer.array(), numRead);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (pendingData) {
            List<ByteBuffer> queue = pendingData.get(socketChannel);
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

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (this.isRunning) {
            try {
                synchronized (pendingChanges) {
                    for (Request change : pendingChanges) {
                        switch (change.type) {
                            case Request.OP_CHANGE:
                                SelectionKey key = change.socket.keyFor(selector);
                                key.interestOps(change.ops);
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

                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() throws IOException {
//        log.info("**** stopping Server4Test instance");
//        try {
            this.selector.close();
            this.isRunning = false;

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("**** stopped Server4Test instance");
    }

}

package org.spbgu.pmpu.athynia.common.network;

import junit.framework.JUnit4TestAdapter;
import static junit.framework.TestCase.fail;
import org.junit.Before;
import org.junit.Test;
import org.spbgu.pmpu.athynia.common.network.impl.SocketOpenerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class SocketOpenerTest {
    private String nicName;

    @Before
    public void setupEnvironment() {
        String osName = System.getProperty("os.name");
        if (osName.equals("Mac OS X")) {
            nicName = "lo0";
        } else {
            nicName = "lo";
        }
    }

    @Test(timeout = 10000)
    public void testServerSocket() {
        SocketOpener socketOpener = null;
        try {
            socketOpener = new SocketOpenerImpl(nicName, "/127.0.0.1");
        } catch (IOException e) {
            fail("Can't create socket manager: " + e);
        }
        int port = 20000;
        ServerSocket serverSocket = null;
        try {
            serverSocket = socketOpener.openServerSocketOnDefaultNic(port);
        } catch (IOException e) {
            fail("Can't create server socket socket: " + e);
        }
        final ServerSocket serverSocket1 = serverSocket;
        startServerSocketAcceptor(serverSocket1);
        Socket socket = null;
        try {
            socket = new Socket();
            socket.bind(new InetSocketAddress(serverSocket1.getInetAddress(), 0));
            socket.connect(new InetSocketAddress("127.0.0.1", port));
        } catch (IOException e) {
            fail("Can't communicate with server: " + e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                serverSocket.close();
            } catch (IOException e) {
                fail("Can't close sockets: " + e);
            }
        }
    }

    @Test(timeout = 10000)
    public void testSocket() {
        SocketOpener socketOpener = null;
        final int port = 20000;
        try {
            socketOpener = new SocketOpenerImpl(nicName, "/127.0.0.1");
        } catch (IOException e) {
            fail("Can't create socket manager: " + e);
        }
        Socket socket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = socketOpener.openServerSocketOnDefaultNic(port);
            startServerSocketAcceptor(serverSocket);
            socket = socketOpener.openSocketOnDefaultNic("127.0.0.1", port);
        } catch (IOException e) {
            fail("Can't communicate with server: " + e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                fail("Can't close sockets: " + e);
            }
        }
    }

    private void startServerSocketAcceptor(final ServerSocket serverSocket) {
        Thread socketAcceptor = new Thread(new Runnable() {
            public void run() {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    fail("Can't work with server socket: " + e);
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                        serverSocket.close();
                    } catch (IOException e) {
                        fail("Can't close socket: " + e);
                    }
                }
            }
        });
        socketAcceptor.setDaemon(true);
        socketAcceptor.start();
    }


    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SocketOpenerTest.class);
    }
}

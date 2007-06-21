package org.spbgu.pmpu.athynia.worker.classloader.testServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: Selivanov
 * Date: 22.04.2007
 */
public class Server4Tests implements Runnable {
    ServerSocket listener;
    Socket server;
    Integer port;
    String request;
    String respose;
    final long timeOut = 100;
    private boolean needResponse = false;

    public Server4Tests(Integer port) throws IOException {
        this.port = port;
        try {
            listener = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("new Serv create");
            listener = new ServerSocket(++port);
        }

    }

    @SuppressWarnings({"InfiniteLoopStatement"})
    public void run() {
        try {
            while (true) {
                server = listener.accept();
                Proccessor proccessor = new Proccessor(server);
                Thread t = new Thread(proccessor);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @SuppressWarnings({"StatementWithEmptyBody"})
    public void shutDown() {
        try {
            while (!server.isClosed()) ;
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Proccessor implements Runnable {
        private Socket server;


        Proccessor(Socket server) {
            this.server = server;
        }

        @SuppressWarnings({"InfiniteLoopStatement"})
        public void run() {
            try {
                if (request != null) {
                    if (!needResponse) {
                        PrintStream out = new PrintStream(server.getOutputStream());
                        out.write(request.getBytes());
                        needResponse = true;
                        request = null;
                        out.flush();
                        server.shutdownOutput();
                    }
                }
                if (needResponse) {
                    String s;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    while ((s = reader.readLine()) != null) {
                        System.out.println("Server4Tests$Proccessor: " + s);
                    }
                    needResponse = false;
                    server.shutdownInput();
                }
            } catch (IOException ioe) {
                System.out.println("IOException on socket listen: " + ioe);
                ioe.printStackTrace();
            } finally {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

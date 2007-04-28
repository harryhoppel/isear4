package org.spbgu.pmpu.athynia.central.communications.split.impl;

import org.spbgu.pmpu.athynia.central.broadcast.BroadcastingDaemon;
import org.spbgu.pmpu.athynia.central.communications.split.Splitter;
import org.spbgu.pmpu.athynia.central.communications.events.PacketFactory;
import org.spbgu.pmpu.athynia.central.communications.events.CommunicationPacket;
import org.spbgu.pmpu.athynia.central.network.Client;
import org.spbgu.pmpu.athynia.central.network.ResponseHandler;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: vasiliy
 */
public class SplitterImpl implements Splitter {
    private static final long WAIT_TIMEOUT = 2000;

    public void sendSplittedDataToWorkers(String data) {
        boolean sended = false;
        InetSocketAddress[] workers = new InetSocketAddress[0];
        while (!sended) {
            workers = retrieveWorkers();
            String[] splittedData = splitData(data, workers.length);
            for (int i = 0; i < workers.length; i++) {
                Client client = new Client(workers[i].getAddress(), workers[i].getPort());
                Thread clientThread = new Thread(client);
                clientThread.setDaemon(true);
                clientThread.start();
                ResponseHandler handler = new ResponseHandler();
                sendData(workers[i], splittedData[i], i);
            }
            sended = waitForCompletion(workers, WAIT_TIMEOUT);
        }
        boolean committed = false;
        for (InetSocketAddress worker : workers) {
            sendCommitSignal(worker);
        }
        committed = waitForCompletion(workers, WAIT_TIMEOUT);
        if (!committed) {
            for (InetSocketAddress worker : workers) {
                sendAbortSignal(worker);
            }
        }
    }

    private InetSocketAddress[] retrieveWorkers() {
        InetSocketAddress[] ret;
        synchronized (BroadcastingDaemon.WORKERS_ADDRESSES) {
            ret = new InetSocketAddress[BroadcastingDaemon.WORKERS_ADDRESSES.size()];
            int i = 0;
            for (InetSocketAddress nextSocketAddress : BroadcastingDaemon.WORKERS_ADDRESSES) {
                ret[i++] = new InetSocketAddress(nextSocketAddress.getAddress(), nextSocketAddress.getPort());
            }
        }
        return ret;
    }

    private String[] splitData(String data, int parts) {
        String[] ret = new String[parts];
        for (int i = 0; i < parts; i++) {
            ret[i] = data.substring((i * data.length()) / parts, (i + 1) * data.length() / parts); //todo: test for different sizes
        }
        return ret;
    }

    private void sendData(InetSocketAddress worker, String data, int partNumber) {
        CommunicationPacket packetToSend = PacketFactory.createSendEvent(data, partNumber);
        try {
            Socket socketToWorker = new Socket(worker.getAddress(), worker.getPort());
            OutputStream outputToWorker = socketToWorker.getOutputStream();
            packetToSend.write(outputToWorker);
        } catch (IOException e) {
            //ignoring all errors, we'll wait for workers reporting about successful receiving instead
        }
    }

    private boolean waitForCompletion(InetSocketAddress[] workers, long timeout) {
        long currentTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - currentTime < timeout) {
            for (InetSocketAddress inetSocketAddress : workers) {

            }
        }
        return true;
    }

    private void sendCommitSignal(InetSocketAddress workers) {

    }

    private void sendAbortSignal(InetSocketAddress worker) {
    }
}

package org.spbgu.pmpu.athynia.central.network.communications.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.communications.WorkersExecutorSender;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class WorkersExecutorSenderImpl implements WorkersExecutorSender {
    private static final Logger LOG = Logger.getLogger(WorkersExecutorSenderImpl.class);

    public boolean runExecutorOnWorker(Worker worker, String executorClassName) {
        BufferedOutputStream outputToWorker = null;
        try {
            Socket socketToWorker = worker.openSocket();
            outputToWorker = new BufferedOutputStream(socketToWorker.getOutputStream());
            LOG.debug("Sending class: " + executorClassName + " to " + socketToWorker.getInetAddress() + ":" + socketToWorker.getPort());
            outputToWorker.write(("loadClass:" + executorClassName).getBytes("UTF-8"));
            outputToWorker.flush();
            return true;
        } catch (IOException e) {
            LOG.error("Can't send executor class name(" + executorClassName + ") to worker: " + worker.getFullAddress(), e);
            return false;
        } finally {
            try {
                if (outputToWorker != null) {
                    outputToWorker.close();
                }
                worker.closeSocket();
            } catch (IOException e) {
                LOG.error("Can't close output socket/stream to worker: " + worker.getFullAddress(), e);
            }
        }
    }
}
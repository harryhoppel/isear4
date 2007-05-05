package org.spbgu.pmpu.athynia.central.communications.impl;

import org.spbgu.pmpu.athynia.central.communications.Worker;
import org.spbgu.pmpu.athynia.central.communications.WorkersExecutorSender;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class WorkersExecutorSenderImpl implements WorkersExecutorSender {
    private static final Logger LOG = Logger.getLogger(WorkersExecutorSenderImpl.class);

    public boolean runExecutorOnWorker(Worker worker, String executorClassName) {
        Socket socketToWorker = null;
        BufferedOutputStream outputToWorker = null;
        try {
            socketToWorker = worker.getSocket();
            outputToWorker = new BufferedOutputStream(socketToWorker.getOutputStream());
            LOG.debug("Sending class: " + executorClassName + " to " + socketToWorker.getInetAddress() + ":" + socketToWorker.getPort());
            outputToWorker.write(("loadClass:" + executorClassName).getBytes("UTF-8"));
            outputToWorker.flush();
            return true;
        } catch (IOException e) {
            LOG.error("Can't send executor class name(" + executorClassName + ") to worker: " + worker.getSocket().getInetAddress() + ":" + worker.getSocket().getPort(), e);
            return false;
        } finally {
            try {
                if (outputToWorker != null) {
                    outputToWorker.close();
                }
                if (socketToWorker != null) {
                    socketToWorker.close();
                }
            } catch (IOException e) {
                LOG.error("Can't close output socket/stream to worker: " + worker.getSocket().getInetAddress() + ":" + worker.getSocket().getPort(), e);
            }
        }
    }
}

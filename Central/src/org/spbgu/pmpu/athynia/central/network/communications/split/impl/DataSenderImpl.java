package org.spbgu.pmpu.athynia.central.network.communications.split.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.communications.CommunicationConstants;
import org.spbgu.pmpu.athynia.central.network.communications.WorkersExecutorSender;
import org.spbgu.pmpu.athynia.central.network.communications.common.Abort;
import org.spbgu.pmpu.athynia.central.network.communications.common.Commit;
import org.spbgu.pmpu.athynia.central.network.communications.impl.WorkersExecutorSenderImpl;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSender;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.central.network.communications.split.SplitReceiver;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class DataSenderImpl implements DataSender {
    //todo: should we send this from multiple threads? --> profile that
    //todo: where to synchronize - right now all sync is done on Central-side
    private static final Logger LOG = Logger.getLogger(DataSenderImpl.class);

    private final WorkersExecutorSender workersExecutorSender = new WorkersExecutorSenderImpl();

    public boolean sendData(String key, String value, Worker[] workers) {
        DataSplitter dataSplitter = new DataSplitterImpl();
        String[] splittedData = dataSplitter.splitData(value, workers.length);
        for (int i = 0; i < workers.length; i++) {
            sendDataTask(workers[i], key, splittedData[i], i, workers.length);
        }
        LOG.debug("Data was sent to workers");
        boolean sended = waitForCompletion(workers);
        LOG.debug("Operation \"Wait for completion\" completed");
        if (!sended) {
            LOG.debug("Data wasn't committed");
            return false;
        }
        for (Worker worker : workers) {
            sendCommitTask(worker);
        }
        boolean committed = waitForCompletion(workers);
        if (!committed) {
            LOG.debug("Data wasn't committed");
            for (Worker worker : workers) {
                sendAbortTask(worker);
                try {
                    worker.closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            return false;
        }
        for (Worker worker : workers) {
            try {
                worker.closeSocket();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        LOG.debug("Data was committed successfully");
        return true;
    }

    private void sendDataTask(Worker worker, String key, String data, int particularPartNumber, int wholePartsQuantity) {
        workersExecutorSender.runExecutorOnWorker(worker, SplitReceiver.class.getName());
        BufferedOutputStream outputToWorker;
        try {
            Socket workersSocket = worker.openSocket();
            LOG.debug("Trying to send data to worker: " + worker.getFullAddress().getHostName() + ":" + worker.getMainPort());
            outputToWorker = new BufferedOutputStream(workersSocket.getOutputStream());
            outputToWorker.write(new JoinPartImpl(key, data, particularPartNumber, wholePartsQuantity).toBinaryForm());
            outputToWorker.flush();
        } catch (IOException e) {
            LOG.warn("Can't send data to worker: " + worker.getFullAddress(), e);
        }
    }

    private boolean waitForCompletion(Worker[] workers) {
        synchronized (this) {
            try {
                wait(CommunicationConstants.CENTRAL_WAIT_FOR_COMPLETION_TIMEOUT);
            } catch (InterruptedException e) {/*ignore*/}
        }
        for (Worker worker : workers) {
            Socket socket = null;
            try {
                socket = worker.openSocket();
            } catch (IOException e) {
                LOG.warn("Can't open socket to worker: " + worker.getFullAddress(), e);
            }
            byte[] buffer = new byte[CommunicationConstants.OK_STRING_SIZE_IN_BYTES_IN_UTF8];
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                inputStream.read(buffer);
                if (!new String(buffer, "UTF-8").equals("OK")) return false;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {/*ignore*/}
                }
            }
        }
        return true;
    }

    private void sendCommitTask(Worker worker) {
        workersExecutorSender.runExecutorOnWorker(worker, Commit.class.getName());
    }

    private void sendAbortTask(Worker worker) {
        workersExecutorSender.runExecutorOnWorker(worker, Abort.class.getName());
    }
}

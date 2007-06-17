package org.spbgu.pmpu.athynia.central.network.communications.split.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.communications.WorkersExecutorSender;
import org.spbgu.pmpu.athynia.common.network.communications.common.Abort;
import org.spbgu.pmpu.athynia.common.network.communications.common.Commit;
import org.spbgu.pmpu.athynia.central.network.communications.impl.WorkersExecutorSenderImpl;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSender;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * User: vasiliy
 */
public class DataSenderImpl<Value> implements DataSender<Value> {
    //todo: should we send this from multiple threads? --> profile that
    //todo: where to synchronize - right now all sync is done on Central-side
    private static final Logger LOG = Logger.getLogger(DataSenderImpl.class);

    private final WorkersExecutorSender workersExecutorSender = new WorkersExecutorSenderImpl();

    private final DataSplitter<Value> dataSplitter;

    public DataSenderImpl(DataSplitter<Value> dataSplitter) {
        this.dataSplitter = dataSplitter;
    }

    public boolean sendData(Class<? extends Executor> klass, String key, Value value, Worker[] workers) {
        LOG.debug("DataSenderImpl.sendData lass= " + klass + ", key = " + key);
        String[] splittedData = dataSplitter.splitData(value, workers.length);
        LOG.debug("Have " + splittedData.length + " parts");
        for (int i = 0; i < workers.length; i++) {
            sendDataTask(klass, workers[i], key, splittedData[i], i, workers.length);
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
        LOG.debug("Commit tasks was sent to workers");
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

    private void sendDataTask(Class<? extends Executor> klass, Worker worker, String key, String data, int particularPartNumber, int wholePartsQuantity) {
        workersExecutorSender.runExecutorOnWorker(worker, klass.getName());
        BufferedOutputStream outputToWorker;
        try {
            Socket workersSocket = worker.openSocket();
            LOG.debug("Trying to send data to worker: " + worker.getFullAddress().getHostName() + ":" + worker.getMainPort());
            outputToWorker = new BufferedOutputStream(workersSocket.getOutputStream());
            LOG.debug("sending key = " + key + ", particularPartNumber = " + particularPartNumber + ", wholePartsQuantity = " + wholePartsQuantity + "datasize = " + data.getBytes().length);
            outputToWorker.write(new JoinPartImpl(key, data, particularPartNumber, wholePartsQuantity).toBinaryForm());
            outputToWorker.flush();
            LOG.debug("outputToWorker flushes");
            workersSocket.shutdownOutput();
        } catch (IOException e) {
            LOG.warn("Can't send data to worker: " + worker.getFullAddress(), e);
        }
    }

    private boolean waitForCompletion(Worker[] workers) {
        long time = System.currentTimeMillis();
        LOG.debug("DataSenderImpl.waitForCompletion");
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
                socket.shutdownInput();
                if (!new String(buffer, "UTF-8").equals("OK")) return false;
            } catch (IOException e) {
                LOG.debug("DataSenderImpl.waitForCompletion finish, returns " + false + ", it takes = " + (System.currentTimeMillis() - time) +" ms");
                return false;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {/*ignore*/}
                }
            }
        }
        LOG.debug("DataSenderImpl.waitForCompletion finish, returns " + true + ", it takes = " + (System.currentTimeMillis() - time) +" ms");
        return true;
    }

    private void sendCommitTask(Worker worker) {
        boolean result = workersExecutorSender.runExecutorOnWorker(worker, Commit.class.getName());
        LOG.debug("Commmit task was successefully send: " + result);
    }

    private void sendAbortTask(Worker worker) {
        workersExecutorSender.runExecutorOnWorker(worker, Abort.class.getName());
    }
}

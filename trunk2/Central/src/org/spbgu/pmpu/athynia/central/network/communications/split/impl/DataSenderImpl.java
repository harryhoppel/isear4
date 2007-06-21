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
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;

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

    public boolean sendData(final Class<? extends Executor> klass, final String key, Value value, final Worker[] workers) {
        LOG.debug("DataSenderImpl.sendData lass= " + klass + ", key = " + key);
        final int wholePartsQuantity = workers.length;
        final String[] splittedData = dataSplitter.splitData(value, wholePartsQuantity);
        LOG.debug("Have " + splittedData.length + " parts");
        java.util.concurrent.Executor dataSender = Executors.newFixedThreadPool(wholePartsQuantity);
        for (int i = 0; i < workers.length; i++) {
            final int part = i;
            dataSender.execute(new Runnable() {
                public void run() {
                    sendDataTask(klass, workers[part], key, splittedData[part], part, wholePartsQuantity);
                }
            });
        }
        LOG.debug("Data was sent to workers");
        boolean sended = waitForCompletion(workers);
        LOG.debug("Operation \"Wait for completion\" completed");
        if (!sended) {
            LOG.debug("Data wasn't committed");
            return false;
        }
        java.util.concurrent.Executor dataCommiter = Executors.newFixedThreadPool(wholePartsQuantity);
        for (int i = 0; i < workers.length; i++) {
            final int part = i;
            dataCommiter.execute(new Runnable() {
                public void run() {
                    sendCommitTask(workers[part]);
                }
            });
        }
        LOG.debug("Commit tasks was sent to workers");
        boolean committed = waitForCompletion(workers);
        if (!committed) {
            LOG.debug("Data wasn't committed");
            java.util.concurrent.Executor dataAborter = Executors.newFixedThreadPool(wholePartsQuantity);
            for (int i = 0; i < workers.length; i++) {
                final int part = i;
                dataAborter.execute(new Runnable() {
                    public void run() {
                        sendAbortTask(workers[part]);
                        try {
                            workers[part].closeSocket();
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                    }
                });
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

    private void restartTask(Class<? extends Executor> klass, String key, Worker[] workers, String[] splittedData) {
        List<Integer> failedIndexes = new ArrayList<Integer>();
        Queue<Worker> completedWorkers = new ArrayDeque<Worker>();
        for (int i = 0; i < workers.length; i++) {
            if (workers[i].getState() == Worker.COMPLETE_STATE) {
                completedWorkers.add(workers[i]);
            }
        }
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
            worker.setState(Worker.PROCESSING_STATE);
        } catch (IOException e) {
            LOG.warn("Can't send data to worker: " + worker.getFullAddress(), e);
            e.printStackTrace();
            worker.setState(Worker.FAIL_STATE);
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
                worker.setState(Worker.FAIL_STATE);
            }
            byte[] buffer = new byte[CommunicationConstants.OK_STRING_SIZE_IN_BYTES_IN_UTF8];
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                inputStream.read(buffer);
                socket.shutdownInput();
                if (!new String(buffer, "UTF-8").equals("OK")) {
                    worker.setState(Worker.FAIL_STATE);
                    return false;
                }
            } catch (IOException e) {
                LOG.debug("DataSenderImpl.waitForCompletion finish, returns " + false + ", it takes = " + (System.currentTimeMillis() - time) + " ms");
                worker.setState(Worker.FAIL_STATE);
                return false;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {/*ignore*/}
                }
            }
            worker.setState(Worker.COMPLETE_STATE);
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

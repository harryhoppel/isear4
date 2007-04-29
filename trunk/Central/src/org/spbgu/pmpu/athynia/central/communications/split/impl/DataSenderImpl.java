package org.spbgu.pmpu.athynia.central.communications.split.impl;

import org.spbgu.pmpu.athynia.central.communications.split.DataSender;
import org.spbgu.pmpu.athynia.central.communications.Worker;
import org.spbgu.pmpu.athynia.central.communications.WorkersExecutorSender;
import org.spbgu.pmpu.athynia.central.communications.CommunicationConstants;
import org.spbgu.pmpu.athynia.central.communications.join.JoinPart;
import org.spbgu.pmpu.athynia.central.communications.join.impl.JoinPartImpl;
import org.spbgu.pmpu.athynia.central.communications.impl.WorkersExecutorSenderImpl;
import org.spbgu.pmpu.athynia.central.communications.common.Commit;
import org.spbgu.pmpu.athynia.central.communications.common.Abort;
import org.spbgu.pmpu.athynia.central.communications.split.SplitReceiver;
import org.spbgu.pmpu.athynia.central.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.central.communications.split.impl.DataSplitterImpl;

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
    private final WorkersExecutorSender workersExecutorSender = new WorkersExecutorSenderImpl();

    public boolean sendData(String key, String value, Worker[] workers) {
        DataSplitter dataSplitter = new DataSplitterImpl();
        String[] splittedData = dataSplitter.splitData(value, workers.length);
        for (int i = 0; i < workers.length; i++) {
            sendDataTask(workers[i], key, splittedData[i], i, workers.length);
        }
        boolean sended = waitForCompletion(workers);
        if (!sended) {
            return false;
        }
        for (Worker worker : workers) {
            sendCommitTask(worker);
        }
        boolean committed = waitForCompletion(workers);
        if (!committed) {
            for (Worker worker : workers) {
                sendAbortTask(worker);
            }
            return false;
        }
        return true;
    }

    private void sendDataTask(Worker worker, String key, String data, int particularPartNumber, int wholePartsQuantity) {
        workersExecutorSender.runExecutorOnWorker(worker, SplitReceiver.class.getName());
        BufferedOutputStream outputToWorker = null;
        try {
            outputToWorker = new BufferedOutputStream(worker.getSocket().getOutputStream());
            outputToWorker.write(new JoinPartImpl(key, data, particularPartNumber, wholePartsQuantity).toBinaryForm());
        } catch (IOException e) {
        } finally {
            if (outputToWorker != null) {
                try {
                    outputToWorker.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private boolean waitForCompletion(Worker[] workers) {
        synchronized(this) {
            try {
                wait(CommunicationConstants.CENTRAL_WAIT_FOR_COMPLETION_TIMEOUT);
            } catch (InterruptedException e) {/*ignore*/}
        }
        for (Worker worker : workers) {
            Socket socket = worker.getSocket();
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

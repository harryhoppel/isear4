package org.spbgu.pmpu.athynia.central.network.communications.join.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.DataJoiner;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.NetworkRunner;
import org.spbgu.pmpu.athynia.central.network.impl.NetworkRunnerImpl;
import org.spbgu.pmpu.athynia.central.network.communications.WorkersExecutorSender;
import org.spbgu.pmpu.athynia.central.network.communications.impl.WorkersExecutorSenderImpl;
import org.spbgu.pmpu.athynia.central.network.communications.join.Joiner;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

/**
 * User: vasiliy
 */
public class JoinerImpl<Value> implements Joiner<Value> {
    //todo: should we send this from multiple threads? --> profile that
    //todo: collisions - what should we do if we have 2 workers and collisions(2 different values for the same key)?
    //todo: null values (is it good to return null, if nothing was found?)

    private static final Logger LOG = Logger.getLogger(JoinerImpl.class);

    private final WorkersExecutorSender workersExecutorSender = new WorkersExecutorSenderImpl();
    private WorkersManager workersManager;
    private final Class<? extends Executor> executorClass;
    private final DataJoiner<Value> dataJoiner;

    public JoinerImpl(WorkersManager workersManager, Class<? extends Executor> klass, DataJoiner<Value> dataJoiner) {
        this.workersManager = workersManager;
        this.executorClass = klass;
        this.dataJoiner = dataJoiner;
    }

    public Value join(String key) {
        Set<Worker> workers = workersManager.getAll();
        for (Worker worker : workers) {
            sendSearchTask(worker, key);
        }
        JoinPart[] retrievedParts = new JoinPart[workers.size()];
        int i = 0;
        for (Worker worker : workers) {
            retrievedParts[i++] = retrieveData(worker);
        }
        dataJoiner.setData(retrievedParts);
        return dataJoiner.getResult();
    }

    private void sendSearchTask(Worker worker, String key) {
        workersExecutorSender.runExecutorOnWorker(worker, executorClass.getName());
        Socket socket;
        try {
            socket = worker.openSocket();
            BufferedOutputStream outputToWorker = new BufferedOutputStream(socket.getOutputStream());
            outputToWorker.write(getIntInUtf8(key.length()).getBytes("UTF-8"));
            outputToWorker.write(key.getBytes("UTF-8"));
            outputToWorker.flush();
            socket.shutdownOutput();
        } catch (IOException e) {
            LOG.warn("Can't communicate with worker: " + worker.getFullAddress(), e);
        }
    }

    private JoinPart retrieveData(Worker worker) {
        try {
            Socket workerSocket = worker.openSocket();
            LOG.debug("Sending request for data to worker: " + workerSocket.getInetAddress() + ":" + workerSocket.getPort());
            BufferedInputStream inputFromWorker = new BufferedInputStream(workerSocket.getInputStream());
            byte[] joinPartLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            inputFromWorker.read(joinPartLengthBuffer);
            int joinPartLength = Integer.parseInt(new String(joinPartLengthBuffer, "UTF-8"));
            byte[] joinPartBuffer = new byte[joinPartLength];
            inputFromWorker.read(joinPartBuffer);
            return new JoinPartImpl(joinPartBuffer);
        } catch (IOException e) {
            return null; //ignore, we will throw an exception later
        }
    }

    private String getIntInUtf8(int i) {
        StringBuffer buffer = new StringBuffer();
        String integer = Integer.toString(i);
        buffer.append(integer);
        while (buffer.length() < org.spbgu.pmpu.athynia.common.CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8) {
            buffer.insert(0, "0");
        }
        return buffer.toString();
    }

}

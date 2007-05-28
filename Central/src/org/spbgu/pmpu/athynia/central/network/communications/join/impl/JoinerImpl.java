package org.spbgu.pmpu.athynia.central.network.communications.join.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.central.network.WorkersManager;
import org.spbgu.pmpu.athynia.central.network.communications.CommunicationConstants;
import org.spbgu.pmpu.athynia.central.network.communications.WorkersExecutorSender;
import org.spbgu.pmpu.athynia.central.network.communications.impl.WorkersExecutorSenderImpl;
import org.spbgu.pmpu.athynia.central.network.communications.join.Joiner;
import org.spbgu.pmpu.athynia.central.network.communications.join.SearchTask;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * User: vasiliy
 */
public class JoinerImpl implements Joiner {
    //todo: should we send this from multiple threads? --> profile that
    //todo: collisions - what should we do if we have 2 workers and collisions(2 different values for the same key)?
    //todo: null values (is it good to return null, if nothing was found?)

    private static final Logger LOG = Logger.getLogger(JoinerImpl.class);

    private final WorkersExecutorSender workersExecutorSender = new WorkersExecutorSenderImpl();
    private WorkersManager workersManager;

    public JoinerImpl(WorkersManager workersManager) {
        this.workersManager = workersManager;
    }

    public String join(String key) {
        Set<Worker> workers = workersManager.getAll();
        for (Worker worker : workers) {
            sendSearchTask(worker, key);
        }
        JoinPart[] retrievedParts = new JoinPart[workers.size()];
        int i = 0;
        for (Worker worker : workers) {
            retrievedParts[i++] = retrieveData(worker);
        }
        return mergeDataParts(retrievedParts);
    }

    private void sendSearchTask(Worker worker, String key) {
        workersExecutorSender.runExecutorOnWorker(worker, SearchTask.class.getName());
        try {
            BufferedOutputStream outputToWorker = new BufferedOutputStream(worker.openSocket().getOutputStream());
            outputToWorker.write(getIntInUtf8(key.length()).getBytes("UTF-8"));
            outputToWorker.write(key.getBytes("UTF-8"));
            outputToWorker.flush();
        } catch (IOException e) {
            LOG.warn("Can't communicate with worker" + worker.getFullAddress(), e);
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

    private String mergeDataParts(JoinPart[] retrievedParts) {
        ArrayList<JoinPart> filteredRetrievedParts = new ArrayList<JoinPart>();
        for (JoinPart retrievedPart : retrievedParts) {
            if (retrievedPart != null && retrievedPart.getWholePartsNumber() != -1) { // -1 - in case of null value found in worker's index
                filteredRetrievedParts.add(retrievedPart);
            }
        }
        if (filteredRetrievedParts.size() == 0) {
            return null;
        }
        if (filteredRetrievedParts.get(0).getWholePartsNumber() != filteredRetrievedParts.size()) {
            return null;
        }
        Collections.sort(filteredRetrievedParts, new Comparator<JoinPart>() {
            public int compare(JoinPart o1, JoinPart o2) {
                return ((Integer) o1.getPartNumber()).compareTo(o2.getPartNumber());
            }
        });
        StringBuffer ret = new StringBuffer();
        for (int index = 0; index < filteredRetrievedParts.size(); index++) {
            JoinPart joinPart = filteredRetrievedParts.get(index);
            if (joinPart.getPartNumber() != index) {
                return null;
            }
            ret.append(joinPart.getValue());
        }
        return ret.toString();
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

package org.spbgu.pmpu.athynia.central.network.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.DataManager;
import org.spbgu.pmpu.athynia.central.network.*;
import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;
import org.spbgu.pmpu.athynia.central.network.communications.join.Joiner;
import org.spbgu.pmpu.athynia.central.network.communications.join.SearchTask;
import org.spbgu.pmpu.athynia.central.network.communications.join.impl.JoinerImpl;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSender;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.central.network.communications.split.impl.DataSenderImpl;
import org.spbgu.pmpu.athynia.common.Executor;

import java.util.Set;

/**
 * User: vasiliy
 */
public class NetworkRunnerImpl<Value> implements NetworkRunner<Value>{
    private Logger LOG = Logger.getLogger(NetworkRunnerImpl.class);


    public void runRemotely(Class<? extends Executor> klass, Data<Value> dataToSend, DataSplitter<Value> dataSplitter) throws CommunicationException {
        LOG.debug("NetworkRunnerImpl.runRemotely");
        Set<Worker> workers = DataManager.getInstance().getData(WorkersManager.class).getAll();
        DataSender<Value> dataSender = new DataSenderImpl<Value>(dataSplitter);
        dataSender.sendData(klass, dataToSend.getKey(), dataToSend.getValue(), workers.toArray(new Worker[0]));
    }
 
    public Value runRemotely(Class<? extends Executor> klass, Data<Value> dataToSend, DataSplitter<Value> dataSplitter, Data<Value> toReceive, DataJoiner dataJoiner) throws CommunicationException {
        LOG.debug("NetworkRunnerImpl.runRemotely");
        Set<Worker> workers = DataManager.getInstance().getData(WorkersManager.class).getAll();
        DataSender<Value> dataSender = new DataSenderImpl<Value>(dataSplitter);
        dataSender.sendData(klass, dataToSend.getKey(), dataToSend.getValue(), workers.toArray(new Worker[0]));
        Joiner<Value> joiner = new JoinerImpl<Value>(DataManager.getInstance().getData(WorkersManager.class), SearchTask.class, dataJoiner);
        return joiner.join(toReceive.getKey());
    }
}

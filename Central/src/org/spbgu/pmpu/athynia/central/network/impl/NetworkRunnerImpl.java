package org.spbgu.pmpu.athynia.central.network.impl;

import org.spbgu.pmpu.athynia.central.DataManager;
import org.spbgu.pmpu.athynia.central.network.*;
import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;
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
public class NetworkRunnerImpl implements NetworkRunner {
    public String runRemotely(Class<? extends Executor> klass, Data dataToSend, DataSplitter dataSplitter, Data toReceive, DataJoiner dataJoiner) throws CommunicationException {
        Set<Worker> workers = DataManager.getInstance().getData(WorkersManager.class).getAll();
        DataSender dataSender = new DataSenderImpl(dataSplitter);
        dataSender.sendData(dataToSend.getKey(), dataToSend.getValue(), workers.toArray(new Worker[0]));
        //todo: delete following
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        JoinerImpl joiner = new JoinerImpl(DataManager.getInstance().getData(WorkersManager.class), SearchTask.class, dataJoiner);
        return joiner.join(toReceive.getKey());
    }
}

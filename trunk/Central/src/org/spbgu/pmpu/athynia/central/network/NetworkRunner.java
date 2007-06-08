package org.spbgu.pmpu.athynia.central.network;

import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.common.Executor;

/**
 * User: vasiliy
 */
public interface NetworkRunner<Value> {
    void runRemotely(Class<? extends Executor> klass, Data<Value> dataToSend, DataSplitter<Value> dataSplitter) throws CommunicationException;

    Value runRemotely(Class<? extends Executor> klass, Data<Value> dataToSend, DataSplitter<Value> dataSplitter, Data<Value> toReceive, DataJoiner<Value> dataJoiner) throws CommunicationException;
}

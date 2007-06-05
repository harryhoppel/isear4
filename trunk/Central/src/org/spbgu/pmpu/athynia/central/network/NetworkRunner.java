package org.spbgu.pmpu.athynia.central.network;

import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.common.Executor;

/**
 * User: vasiliy
 */
public interface NetworkRunner {
    String runRemotely(Class<? extends Executor> klass, Data dataToSend, DataSplitter dataSplitter, Data toReceive, DataJoiner dataJoiner) throws CommunicationException;
}

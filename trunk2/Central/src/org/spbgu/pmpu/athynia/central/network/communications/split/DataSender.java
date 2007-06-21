package org.spbgu.pmpu.athynia.central.network.communications.split;

import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.common.Executor;

/**
 * User: vasiliy
 */
public interface DataSender<Value> {
    boolean sendData(Class<? extends Executor> klass, String key, Value value, Worker[] workers);
}

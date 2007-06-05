package org.spbgu.pmpu.athynia.central.network.communications.split;

import org.spbgu.pmpu.athynia.central.network.Worker;
import org.spbgu.pmpu.athynia.common.Executor;

/**
 * User: vasiliy
 */
public interface DataSender {
    boolean sendData(Class<? extends Executor> klass, String key, String value, Worker[] workers);
}

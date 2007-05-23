package org.spbgu.pmpu.athynia.central.network.communications.split;

import org.spbgu.pmpu.athynia.central.network.Worker;

/**
 * User: vasiliy
 */
public interface DataSender {
    boolean sendData(String key, String value, Worker[] workers);
}

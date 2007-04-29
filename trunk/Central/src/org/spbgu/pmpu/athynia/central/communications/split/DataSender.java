package org.spbgu.pmpu.athynia.central.communications.split;

import org.spbgu.pmpu.athynia.central.communications.Worker;

/**
 * User: vasiliy
 */
public interface DataSender {
    boolean sendData(String key, String value, Worker[] workers);
}

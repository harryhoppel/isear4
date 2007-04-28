package org.spbgu.pmpu.athynia.central.communications.split;

/**
 * User: vasiliy
 */
public interface Splitter {
    void sendSplittedDataToWorkers(String data);
}

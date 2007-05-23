package org.spbgu.pmpu.athynia.central.network.communications.split;

/**
 * User: vasiliy
 */
public interface DataSplitter {
    String[] splitData(String data, int parts);
}

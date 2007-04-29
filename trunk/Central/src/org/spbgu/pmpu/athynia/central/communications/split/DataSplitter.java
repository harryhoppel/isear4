package org.spbgu.pmpu.athynia.central.communications.split;

/**
 * User: vasiliy
 */
public interface DataSplitter {
    String[] splitData(String data, int parts);
}

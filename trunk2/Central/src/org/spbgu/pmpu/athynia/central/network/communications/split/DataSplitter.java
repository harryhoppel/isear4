package org.spbgu.pmpu.athynia.central.network.communications.split;

/**
 * User: vasiliy
 */
public interface DataSplitter<Value> {
    String[] splitData(Value data, int parts);
}

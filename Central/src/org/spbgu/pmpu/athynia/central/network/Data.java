package org.spbgu.pmpu.athynia.central.network;

/**
 * User: vasiliy
 */
public interface Data<Value> {
    String getKey();

    Value getValue();
}

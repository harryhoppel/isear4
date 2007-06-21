package org.spbgu.pmpu.athynia.central.network.impl;

import org.spbgu.pmpu.athynia.central.network.Data;

/**
 * User: vasiliy
 */
public class DataImpl<Value> implements Data<Value> {
    private final String key;
    private final Value value;

    public DataImpl(String key, Value value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }
}

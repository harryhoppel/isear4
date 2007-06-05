package org.spbgu.pmpu.athynia.central.network.impl;

import org.spbgu.pmpu.athynia.central.network.Data;

/**
 * User: vasiliy
 */
public class DataImpl implements Data {
    private final String key;
    private final String value;

    public DataImpl(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

package org.spbgu.pmpu.athynia.central.network.communications.split.impl;

import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;

/**
 * User: vasiliy
 */
public class DataSplitterImpl<Value extends String> implements DataSplitter<Value> {
    public String[] splitData(Value data, int parts) {
        String[] ret = new String[parts];
        for (int i = 0; i < parts; i++) {
            ret[i] = data.substring(i * data.length() / parts, (i + 1) * data.length() / parts); //todo: test for different sizes
        }
        return ret;
    }
}

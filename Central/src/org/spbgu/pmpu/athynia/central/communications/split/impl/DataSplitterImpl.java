package org.spbgu.pmpu.athynia.central.communications.split.impl;

import org.spbgu.pmpu.athynia.central.communications.split.DataSplitter;

/**
 * User: vasiliy
 */
public class DataSplitterImpl implements DataSplitter {
    public String[] splitData(String data, int parts) {
        String[] ret = new String[parts];
        for (int i = 0; i < parts; i++) {
            ret[i] = data.substring(i * data.length() / parts, (i + 1) * data.length() / parts); //todo: test for different sizes
        }
        return ret;
    }
}

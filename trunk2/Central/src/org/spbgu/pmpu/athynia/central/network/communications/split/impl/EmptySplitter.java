package org.spbgu.pmpu.athynia.central.network.communications.split.impl;

import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;

import java.util.Arrays;

/**
 * User: A.Selivanov
 * Date: 07.06.2007
 */
public class EmptySplitter<Value> implements DataSplitter<Value> {
    public String[] splitData(Value data, int parts) {
        String[] result = new String[parts];
        Arrays.fill(result, data.toString());
        return result;
    }
}

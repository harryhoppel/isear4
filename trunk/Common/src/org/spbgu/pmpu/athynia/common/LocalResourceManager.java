package org.spbgu.pmpu.athynia.common;

import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * User: vasiliy
 */
public class LocalResourceManager {
    private long previousDropTime;
    private JoinPart previousJoinPart;

    private Map<String, JoinPart> index = new HashMap<String, JoinPart>();

    public void write(String key, String value, int currentPartNumber, int wholeNumbers, long timeoutUntilDrop) {
        previousDropTime = System.currentTimeMillis() + timeoutUntilDrop;
        previousJoinPart = new JoinPartImpl(key, value, currentPartNumber, wholeNumbers);
    }

    public void commit() {
        if (System.currentTimeMillis() <= previousDropTime) {
            index.put(previousJoinPart.getKey(), previousJoinPart);
        }
    }

    public void abort() {
        previousDropTime = 0;
        previousJoinPart = null;
    }

    public JoinPart search(String key) {
        JoinPart ret = index.get(key);
        if (ret == null) {
            return new JoinPartImpl(key, null, 0, -1);
        } else {
            return ret;
        }
    }
}

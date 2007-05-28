package org.spbgu.pmpu.athynia.common;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: vasiliy
 */
public class LocalResourceManager {
    private static final Logger LOG = Logger.getLogger(LocalResourceManager.class);

    private Map<String, JoinPart> previousJoinParts = Collections.synchronizedMap(new HashMap<String, JoinPart>());

    private Map<String, JoinPart> index = Collections.synchronizedMap(new HashMap<String, JoinPart>());

    public void write(String key, String value, int currentPartNumber, int wholeNumbers, long timeoutUntilDrop) {
        previousJoinParts.put(key, new JoinPartImpl(key, value, currentPartNumber, wholeNumbers));
        LOG.debug("New pre-commit write to index: " + key + " ---> " + value);
    }

    public void commit() {
        index.putAll(previousJoinParts);
        LOG.debug("New index commit");
    }

    public void abort() {
        LOG.debug("Pre-commit writes to index were dropped");
        previousJoinParts.clear();
    }

    public JoinPart search(String key) {
        JoinPart ret = index.get(key);
        if (ret == null) {
            LOG.debug("Nothing was found in index, while searching with key: " + key);
            return new JoinPartImpl(key, "", 0, -1);
        } else {
            return ret;
        }
    }
}

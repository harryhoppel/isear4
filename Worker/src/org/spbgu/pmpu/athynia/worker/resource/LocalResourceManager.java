package org.spbgu.pmpu.athynia.worker.resource;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: vasiliy
 */
@Deprecated
public class LocalResourceManager implements ResourceManager {
    private static final Logger LOG = Logger.getLogger(LocalResourceManager.class);

    private Map<String, JoinPart> previousJoinParts = Collections.synchronizedMap(new HashMap<String, JoinPart>());

    private Map<String, JoinPart> index = Collections.synchronizedMap(new HashMap<String, JoinPart>());

    public void write(String key, String value, int currentPartNumber, int wholeNumbers, long timeoutUntilDrop) {
        previousJoinParts.put(key, new JoinPartImpl(key, value, currentPartNumber, wholeNumbers));
        LOG.debug("New pre-commit write to index: " + key + " ---> " + value);
    }

    public JoinPart remove(String key) {
        return index.remove(key);
    }

    public int getSize() {
        return index.size();
    }

    public void commit() {
        index.putAll(previousJoinParts);
        LOG.debug("New index commit");
    }

    public void abort() {
        LOG.debug("Pre-commit writes to index were dropped");
        previousJoinParts.clear();
    }

    public void write(String key, String value) {
        write(key, value, 0, 0, CommunicationConstants.TIMEOUT_UNTIL_DATA_DROP);
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

    public void close() {
        //nothing
    }
}

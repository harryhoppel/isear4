package org.spbgu.pmpu.athynia.central.communications.join;

import org.spbgu.pmpu.athynia.central.communications.CommunicationException;

/**
 * User: vasiliy
 */
public interface Joiner {
    String join(String key) throws CommunicationException;
}

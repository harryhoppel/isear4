package org.spbgu.pmpu.athynia.central.network.communications.join;

import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;

/**
 * User: vasiliy
 */
public interface Joiner {
    String join(String key) throws CommunicationException;
}

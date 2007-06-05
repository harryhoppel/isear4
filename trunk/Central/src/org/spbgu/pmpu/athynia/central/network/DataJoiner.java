package org.spbgu.pmpu.athynia.central.network;

import org.spbgu.pmpu.athynia.common.JoinPart;

/**
 * User: vasiliy
 */
public interface DataJoiner {
//    void setData(String[] receivedData);
    void setData(JoinPart[] receivedData);

    String getResult();
}

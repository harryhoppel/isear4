package org.spbgu.pmpu.athynia.central.communications.join;

/**
 * User: vasiliy
 */
public interface JoinPart {
    String getKey();
    String getValue();

    int getPartNumber();
    int getWholePartsNumber();

    byte[] toBinaryForm();
}

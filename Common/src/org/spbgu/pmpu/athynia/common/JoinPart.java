package org.spbgu.pmpu.athynia.common;

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

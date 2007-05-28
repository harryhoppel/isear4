package org.spbgu.pmpu.athynia.common.impl;

import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;

import java.io.UnsupportedEncodingException;

/**
 * User: vasiliy
 */
public class JoinPartImpl implements JoinPart {
    private final String key;
    private final String value;
    private final int partNumber;
    private final int wholePartsNumber;

    public JoinPartImpl(String key, String value, int partNumber, int wholePartsNumber) {
        this.key = key;
        this.value = value;
        this.partNumber = partNumber;
        this.wholePartsNumber = wholePartsNumber;
    }

    public JoinPartImpl(byte[] binaryForm) {
        try {
            int currentIndexInBinaryForm = 0;
            wholePartsNumber = Integer.parseInt(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8"));
            currentIndexInBinaryForm += CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8;
            partNumber = Integer.parseInt(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8"));
            currentIndexInBinaryForm += CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8;
            int keyLength = Integer.parseInt(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8"));
            currentIndexInBinaryForm += CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8;
            key = new String(binaryForm,
                currentIndexInBinaryForm,
                keyLength, "UTF-8");
            currentIndexInBinaryForm += keyLength;
            int valueLength = Integer.parseInt(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8"));
            value = new String(binaryForm,
                currentIndexInBinaryForm,
                valueLength, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Can't encode in UTF-8", e);
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public int getWholePartsNumber() {
        return wholePartsNumber;
    }

    public byte[] toBinaryForm() {
        try {
            int keyLength = key.getBytes("UTF-8").length;
            int valueLength = value.getBytes("UTF-8").length;
            String ret = getIntInUtf8(wholePartsNumber)
                    + getIntInUtf8(partNumber)
                    + getIntInUtf8(keyLength)
                    + key
                    + getIntInUtf8(valueLength)
                    + value;
            return (ret.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    private String getIntInUtf8(int i) {
        StringBuffer buffer = new StringBuffer();
        String integer = Integer.toString(i);
        buffer.append(integer);
        while (buffer.length() < CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8) {
            buffer.insert(0, "0");
        }
        return buffer.toString();
    }
}

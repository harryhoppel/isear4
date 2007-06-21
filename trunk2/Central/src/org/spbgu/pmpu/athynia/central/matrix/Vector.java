package org.spbgu.pmpu.athynia.central.matrix;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.JoinPart;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class Vector implements JoinPart {
    public static final String DEFAULT_VECTOR_NAME = "Vector";

    private static final Logger LOG = Logger.getLogger(Vector.class);

    private double[] elements;
    private int vectorIndex = 0;
    private int wholeMatrixSize = 0;

    public Vector(double[] elements) {
        this.elements = elements;
    }

    public Vector(double[] elements, int vectorIndex, int wholeMatrixSize) {
        this.elements = elements;
        this.vectorIndex = vectorIndex;
        this.wholeMatrixSize = wholeMatrixSize;
    }


    public Vector(byte[] binaryForm) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Data length is: " + binaryForm.length + "; received string: " + new String(binaryForm));
        }
        try {
//          vectorIndex; wholeMatrixSize; ValueSize value
            int currentIndexInBinaryForm = 0;
            vectorIndex = Integer.parseInt(decodeString(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8")));
            currentIndexInBinaryForm += CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8;

            wholeMatrixSize = Integer.parseInt(decodeString(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8")));
            currentIndexInBinaryForm += CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8;

            int valueSize = Integer.parseInt(decodeString(new String(binaryForm,
                currentIndexInBinaryForm,
                CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8, "UTF-8")));
            currentIndexInBinaryForm += CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8;

            elements = new double[valueSize];
            for (int i = 0; i < valueSize; i++) {
                elements[i] = Double.parseDouble(decodeString(new String(binaryForm,
                    currentIndexInBinaryForm,
                    CommunicationConstants.DOUBLE_LENGTH_IN_BYTES_IN_UTF8, "UTF-8")));
                currentIndexInBinaryForm += CommunicationConstants.DOUBLE_LENGTH_IN_BYTES_IN_UTF8;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Can't encode in UTF-8", e);
        }
    }

    public Vector(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, Matrix.DELIMETERS);
        int size = Integer.valueOf(tokenizer.nextToken());
        elements = new double[size];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = Double.valueOf(tokenizer.nextToken());
        }
    }


    public byte[] toBinaryForm() {
        try {
            StringBuffer ret = new StringBuffer();
            ret.append(getIntInUtf8(vectorIndex));
            ret.append(getIntInUtf8(wholeMatrixSize));
            ret.append(getIntInUtf8(size()));
            for (double element : elements) {
                ret.append(getDoubleInUtf8(element));
            }
            return (ret.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); //todo
            return null;
        }
    }

    public double getElement(int index) {
        return elements[index];
    }

    public double[] getElements() {
        return elements;
    }

    public void setElements(double[] elements) {
        this.elements = elements;
    }

    public int size() {
        return elements.length;
    }

    public String getKey() {
        return "vector";
    }

    public String getValue() {
        StringBuffer result = new StringBuffer();
        for (double element : elements) {
            result.append(element).append(",");
        }
        return result.toString();
    }

    /**
     * @return index of current vector
     */
    public int getPartNumber() {
        return vectorIndex;
    }

    /**
     * @return size of the matrix
     */
    public int getWholePartsNumber() {
        return wholeMatrixSize;
    }

    public int getVectorIndex() {
        return vectorIndex;
    }

    public void setVectorIndex(int vectorIndex) {
        this.vectorIndex = vectorIndex;
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

    private String getDoubleInUtf8(double d) {
        StringBuffer buffer = new StringBuffer();
        String integer = Double.toString(d);
        buffer.append(integer);
        while (buffer.length() < CommunicationConstants.DOUBLE_LENGTH_IN_BYTES_IN_UTF8) {
            buffer.insert(0, "0");
        }
        return buffer.toString();
    }

    private String decodeString(String s) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.substring(0, 1).equals("0") && buffer.length() > 1) {
            buffer.delete(0, 1);
        }
        return buffer.toString();
    }

    public int getSize() {
        return elements.length;
    }

    public String toString() {
        StringBuffer result = new StringBuffer(String.valueOf(elements.length));
        result.append(" ");
        for (double element : elements) {
            result.append(element).append(" ");
        }
        result.append('\n');
        return result.toString();
    }
}

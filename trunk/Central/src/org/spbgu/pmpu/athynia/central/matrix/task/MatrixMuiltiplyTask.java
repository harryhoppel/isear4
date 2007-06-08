package org.spbgu.pmpu.athynia.central.matrix.task;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.Vector;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: A.Selivanov
 * Date: 07.06.2007
 */
public class MatrixMuiltiplyTask implements Executor {
    private Logger LOG = Logger.getLogger(MatrixMuiltiplyTask.class);

    public void execute(InputStream fromServer, OutputStream toServer, ResourceManager manager) throws ExecutorException {
        System.out.println("Loading " + MatrixMuiltiplyTask.class.getName());
        try {
            byte[] totalSplitNumbersBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(totalSplitNumbersBuffer);
            int totalSplitNumber = Integer.parseInt(decodeStringWithInteger(new String(totalSplitNumbersBuffer, "UTF-8")));
            LOG.debug("totalSplitNumber = " + totalSplitNumber + "; from buffer: " + new String(totalSplitNumbersBuffer));

            byte[] particularSplitNumberBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(particularSplitNumberBuffer);
            int partNumber = Integer.parseInt(decodeStringWithInteger(new String(particularSplitNumberBuffer, "UTF-8")));
            LOG.debug("partNumber = " + partNumber);

            byte[] keyLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(keyLengthBuffer);
            int keyLength = Integer.parseInt(decodeStringWithInteger(new String(keyLengthBuffer, "UTF-8")));
            byte[] keyBuffer = new byte[keyLength];
            fromServer.read(keyBuffer);
            String key = new String(keyBuffer, "UTF-8");
            LOG.debug("Received key: " + key);

            byte[] valueLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(valueLengthBuffer);

            BufferedReader reader = new BufferedReader(new InputStreamReader(fromServer));
            String s;
            List<Vector> vectors = new ArrayList<Vector>();
            while ((s = reader.readLine()) != null) {
                vectors.add(new Vector(s));
            }

            File out = new File("matrixA");
            Matrix matrix = new Matrix(out);
            int size = matrix.size();

            List<Vector> result = new ArrayList<Vector>();
            int startPos = getStartIndex(size, partNumber, totalSplitNumber);

            for (int index = startPos; index < vectors.size() + startPos; index++){
                double[] elements = new double[size];
                for (int i = 0; i < size; i++) {
                    elements[i] = computeMultiply(vectors.get(index - startPos), matrix.getColumn(index));
                }
                result.add(new Vector(elements));
            }

            StringBuffer buf = new StringBuffer();
            for (Vector aResult : result) {
                buf.append(aResult);
            }

            manager.write("matrix-multiply", buf.toString(), partNumber, totalSplitNumber, 0);

            toServer.write("OK".getBytes("UTF-8"));
            toServer.flush();
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Unsupported encoding UTF-8", e);
        } catch (IOException e) {
            LOG.warn("Can't communicate with central", e);
        }
    }

    public static int getStartIndex(int size, int partNumber, int parts) {
        int[] ints = new int[parts];
        int part = (int) (Math.floor(size / parts) + size % parts);
        ints[0] = 0;
        ints[1] = part;
        int elseParts = 0;
        if (parts != 1) {
            elseParts = (size - part) / (parts - 1);
        }
        for (int i = 2; i < parts; i++) {
            ints[i] = part + (i-1) * elseParts;
        }
        return ints[partNumber];
    }

    private double computeMultiply(Vector column, Vector raw) {
        double result = 0.0;
        for (int i = 0; i < column.size(); i++) {
            result += column.getElement(i) * raw.getElement(i);
        }
        return result;
    }

    private String decodeStringWithInteger(String s) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.substring(0, 1).equals("0") && buffer.length() > 1) {
            buffer.delete(0, 1);
        }
        return buffer.toString();
    }
}

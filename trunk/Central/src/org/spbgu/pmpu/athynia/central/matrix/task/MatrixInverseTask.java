package org.spbgu.pmpu.athynia.central.matrix.task;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.Vector;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class MatrixInverseTask implements Executor {
    private static final Logger LOG = Logger.getLogger(MatrixInverseTask.class);
    private Matrix matrix;
    private double det;

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
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
            List<Integer> indexes = new ArrayList<Integer>();
            while ((s = reader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(s, ",");
                while (tokenizer.hasMoreTokens()) {
                    indexes.add(Integer.parseInt(tokenizer.nextToken()));
                }
            }

            File out = new File(key);
            matrix = new Matrix(out);
            int size = matrix.size();
            LOG.debug("Matrix loading, size = " + size);

            det = matrix.determinate();

            List<Vector> result = new ArrayList<Vector>();

            for (Integer index : indexes) {
                double[] elements = new double[size];
                for (int i = 0; i < size; i++) {
                    elements[i] = compute(index, i);
                }
                result.add(new Vector(elements));
            }

            StringBuffer buf = new StringBuffer();
            for (Vector aResult : result) {
                buf.append(aResult);
            }

            manager.write("InverseMatrix", buf.toString(), partNumber, totalSplitNumber, 0);

            toServer.write("OK".getBytes("UTF-8"));
            toServer.flush();
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Unsupported encoding UTF-8", e);
        } catch (IOException e) {
            LOG.warn("Can't communicate with central", e);
        }
    }

    private double compute(Integer i, Integer j) {
        double minor = matrix.getMinorDet(i, j);
        return Matrix.getDouble(minor / det);
    }

    private String decodeStringWithInteger(String s) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.substring(0, 1).equals("0") && buffer.length() > 1) {
            buffer.delete(0, 1);
        }
        return buffer.toString();
    }
}
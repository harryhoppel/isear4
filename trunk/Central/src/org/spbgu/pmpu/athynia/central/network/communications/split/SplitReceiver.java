package org.spbgu.pmpu.athynia.central.network.communications.split;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * User: vasiliy
 */
public class SplitReceiver implements Executor {
    private static final Logger LOG = Logger.getLogger(SplitReceiver.class);

    public void execute(InputStream fromServer, OutputStream toServer, ResourceManager manager) throws ExecutorException {
        System.out.println("Hello, world!");
        try {
            byte[] totalSplitNumbersBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(totalSplitNumbersBuffer);
            int totalSplitNumber = Integer.parseInt(decodeStringWithInteger(new String(totalSplitNumbersBuffer, "UTF-8")));
            LOG.debug("totalSplitNumber = " + totalSplitNumber + "; from buffer: " + new String(totalSplitNumbersBuffer));

            byte[] particularSplitNumberBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(particularSplitNumberBuffer);
            int particularSplitNumber = Integer.parseInt(decodeStringWithInteger(new String(particularSplitNumberBuffer, "UTF-8")));

            byte[] keyLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(keyLengthBuffer);
            int keyLength = Integer.parseInt(decodeStringWithInteger(new String(keyLengthBuffer, "UTF-8")));
            byte[] keyBuffer = new byte[keyLength];
            fromServer.read(keyBuffer);
            String key = new String(keyBuffer, "UTF-8");
            LOG.debug("Received key: " + key);

            byte[] valueLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(valueLengthBuffer);
            int valueLength = Integer.parseInt(decodeStringWithInteger(new String(valueLengthBuffer, "UTF-8")));
            byte[] valueBuffer = new byte[valueLength];
            fromServer.read(valueBuffer);
            String value = new String(valueBuffer, "UTF-8");
            LOG.debug("Received value: " + value);

            manager.write(key, value, particularSplitNumber, totalSplitNumber, CommunicationConstants.TIMEOUT_UNTIL_DATA_DROP);
            toServer.write("OK".getBytes("UTF-8"));
            toServer.flush();
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Unsupported encoding UTF-8", e);
        } catch (IOException e) {
            LOG.warn("Can't communicate with central", e);
        }
    }

    private String decodeStringWithInteger(String s) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.substring(0, 1).equals("0") && buffer.length() > 1) {
            buffer.delete(0, 1);
        }
        return buffer.toString();
    }
}

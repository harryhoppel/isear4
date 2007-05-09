package org.spbgu.pmpu.athynia.central.communications.split;

import org.spbgu.pmpu.athynia.central.communications.CommunicationConstants;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.LocalResourceManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class SplitReceiver implements Executor {
    private static final Logger LOG = Logger.getLogger(SplitReceiver.class);

    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        System.out.println("Hello, world!");
        try {
            byte[] keyLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(keyLengthBuffer);
            int keyLength = Integer.parseInt(new String(keyLengthBuffer, "UTF-8"));
            byte[] keyBuffer = new byte[keyLength];
            fromServer.read(keyBuffer);
            String key = new String(keyBuffer, "UTF-8");
            LOG.debug("Received key: " + key);
            byte[] valueLengthBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(valueLengthBuffer);
            int valueLength = Integer.parseInt(new String(valueLengthBuffer, "UTF-8"));
            byte[] valueBuffer = new byte[valueLength];
            fromServer.read(valueBuffer);
            String value = new String(valueBuffer, "UTF-8");
            LOG.debug("Received value: " + value);
            byte[] particularSplitNumberBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(particularSplitNumberBuffer);
            int particularSplitNumber = Integer.parseInt(new String(particularSplitNumberBuffer, "UTF-8"));
            byte[] totalSplitNumbersBuffer = new byte[CommunicationConstants.INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(totalSplitNumbersBuffer);
            int totalSplitNumber = Integer.parseInt(new String(totalSplitNumbersBuffer, "UTF-8"));
            manager.write(key, value, particularSplitNumber, totalSplitNumber, CommunicationConstants.TIMEOUT_UNTIL_DATA_DROP);
            toServer.write("OK".getBytes("UTF-8"));
            toServer.flush();
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Unsupported encoding UTF-8", e);
        } catch (IOException e) {
            LOG.warn("Can't communicate with central", e);
        }
    }
}

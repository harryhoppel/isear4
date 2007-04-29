package org.spbgu.pmpu.athynia.central.communications.join;

import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;
import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;
import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class SearchTask implements Executor {
    private static final int INTEGER_LENGTH_IN_BYTES_IN_UTF8 = 8;

    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        try {
            byte[] keyLengthBuffer = new byte[INTEGER_LENGTH_IN_BYTES_IN_UTF8];
            fromServer.read(keyLengthBuffer);
            int keyLength = Integer.parseInt(new String(keyLengthBuffer, "UTF-8"));
            byte[] keyBuffer = new byte[keyLength];
            fromServer.read(keyBuffer);
            String key = new String(keyBuffer, "UTF-8");
            JoinPart joinPart = manager.search(key);
            toServer.write(joinPart.toBinaryForm());
        } catch (IOException e) {
            //ignore
        }
    }
}

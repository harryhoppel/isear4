package org.spbgu.pmpu.athynia.central.network.communications.common;

import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class Abort implements Executor {
    private static final Logger LOG = Logger.getLogger(Abort.class);

    public void execute(InputStream fromServer, OutputStream toServer, ResourceManager manager) throws ExecutorException {
        manager.abort();
        try {
            toServer.write("OK".getBytes("UTF-8"));
            toServer.flush();
        } catch (IOException e) {
            LOG.warn("Can't communicate with central");
        }
    }
}

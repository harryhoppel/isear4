package org.spbgu.pmpu.athynia.central.network.communications.common;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.LocalResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: vasiliy
 */
public class Commit implements Executor {
    private static final Logger LOG = Logger.getLogger(Commit.class);

    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        manager.commit();
        try {
            toServer.write("OK".getBytes("UTF-8"));
        } catch (IOException e) {
            LOG.warn("Can't communicate with central");
        }
    }
}

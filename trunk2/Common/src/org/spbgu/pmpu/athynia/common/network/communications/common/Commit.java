package org.spbgu.pmpu.athynia.common.network.communications.common;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: vasiliy
 */
public class Commit implements Executor {
    private static final Logger LOG = Logger.getLogger(Commit.class);

    public void execute(InputStream fromServer, OutputStream toServer, ResourceManager manager) throws ExecutorException {
        System.out.println("Loading " + Commit.class.getName());
        manager.commit();
        LOG.info("commited");
        try {
            toServer.write("OK".getBytes("UTF-8"));
            toServer.flush();
        } catch (IOException e) {
            LOG.warn("Can't communicate with central", e);
        }
    }
}

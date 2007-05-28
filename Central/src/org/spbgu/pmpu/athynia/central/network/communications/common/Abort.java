package org.spbgu.pmpu.athynia.central.network.communications.common;

import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: vasiliy
 */
public class Abort implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, ResourceManager manager) throws ExecutorException {
        manager.abort();
    }
}

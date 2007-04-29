package org.spbgu.pmpu.athynia.central.communications.common;

import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;
import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;
import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: vasiliy
 */
public class Abort implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        manager.abort();
    }
}

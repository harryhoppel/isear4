package org.spbgu.pmpu.athynia.central.communications.common;

import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;
import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;
import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * User: vasiliy
 */
public class Commit implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        manager.commit();
        try {
            toServer.write("OK".getBytes("UTF-8"));
        } catch (IOException e) {
        }
    }
}

package org.spbgu.pmpu.athynia.worker.classloader.testclasses;

import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;
import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;
import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: Selivanov
 * Date: 20.04.2007
 */
public class Test1 implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        System.out.println("good test");
    }
}

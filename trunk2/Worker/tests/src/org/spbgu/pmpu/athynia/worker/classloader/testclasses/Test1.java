package org.spbgu.pmpu.athynia.worker.classloader.testclasses;


import org.spbgu.pmpu.athynia.common.Executor;
import org.spbgu.pmpu.athynia.common.ExecutorException;
import org.spbgu.pmpu.athynia.common.ResourceManager;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: Selivanov
 * Date: 20.04.2007
 */
public class Test1 implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, ResourceManager manager) throws ExecutorException {
        System.out.println("good test");
    }
}

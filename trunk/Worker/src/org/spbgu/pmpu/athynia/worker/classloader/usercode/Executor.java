package org.spbgu.pmpu.athynia.worker.classloader.usercode;

import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;
import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 * Testing version
 * </pre>
 * Author: Selivanov
 * Date: 25.03.2007
 * Time: 21:26:53
 */
public interface Executor {
    void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException;
}

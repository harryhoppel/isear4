package org.spbgu.pmpu.athynia.worker.classloader.usercode.simple;

import org.spbgu.pmpu.athynia.worker.classloader.usercode.TestClassLoader;

/**
 * Author: Selivanov
 * Date: 26.03.2007
 * Time: 2:29:09
 */
public class TestClassLoaderImpl implements TestClassLoader {
    public String printHelloWorld() {
        return "Hello world!!";
    }
}

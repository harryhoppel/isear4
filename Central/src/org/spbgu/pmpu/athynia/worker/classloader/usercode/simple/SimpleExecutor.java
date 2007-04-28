package org.spbgu.pmpu.athynia.worker.classloader.usercode.simple;

import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;
import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;
import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Author: Selivanov
 * Date: 25.03.2007
 * Time: 21:29:22
 */
public class SimpleExecutor implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fromServer));
        String s;
        try {
            while((s = reader.readLine())!= null){
                System.out.println(s);
//                toServer.write(s.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
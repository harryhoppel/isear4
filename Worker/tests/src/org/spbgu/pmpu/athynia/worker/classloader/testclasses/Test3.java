package org.spbgu.pmpu.athynia.worker.classloader.testclasses;

import org.spbgu.pmpu.athynia.worker.classloader.ExecutorException;
import org.spbgu.pmpu.athynia.worker.classloader.LocalResourceManager;
import org.spbgu.pmpu.athynia.worker.classloader.usercode.Executor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * User: Selivanov
 * Date: 21.04.2007
 */
public class Test3 implements Executor {
    public void execute(InputStream fromServer, OutputStream toServer, LocalResourceManager manager) throws ExecutorException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fromServer));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(toServer));
        String s;
        try {
            System.out.println("Test3.execute");
            while ((s = reader.readLine()) != null) {
                System.out.println("Test3:" + s);
                writer.write(s);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Test3:Complete");
    }
}

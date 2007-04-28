package org.spbgu.pmpu.athynia.central.classloader;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * User: Selivanov
 * Date: 14.04.2007
 */
public class ZipClassReader {
    private static final Logger LOG = Logger.getLogger(ZipClassReader.class);
    private ClassDefiner definer;
    private Hashtable<String, byte[]> cache;

    public ZipClassReader(File homeDirectory) throws MalformedURLException {
        definer = new ClassDefiner(new URL[]{homeDirectory.toURI().toURL()});
        cache = new Hashtable<String, byte[]>();
    }

    public byte[] getClassFromCache(String className) {
        System.out.println("ZipClassReader.getClassFromCache: " + className);
        if (cache.containsKey(className))
          return zipBytes(cache.get(className));
        else return new byte[1];
    }

    public void readZipFile(File zipFile) {
        ZipEntry zipEntry;
        ZipInputStream in;

        try {
            in = new ZipInputStream(new FileInputStream(zipFile));
            while ((zipEntry = in.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName().replaceAll("/", ".");
                if (zipEntryName.endsWith(".class")) {
                    byte[] classBytes = getClassBytes(in);
                    cache.put(definer.getClassName(classBytes), classBytes);
                }
                // else ignore it; it could be an image or audio zipFile
                in.closeEntry();
            }
            System.out.println("cache.size() = " + cache.size());
        } catch (IOException ioe) {
            LOG.warn("Badly formatted zip file", ioe);
        }
    }

    private byte[] zipBytes(byte[] toCompress) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] retval = new byte[0];
        try {
            BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
            bufos.write(toCompress);
            bufos.close();
            retval = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            LOG.warn("Error while zipping: " + e);
        }
        return retval;
    }

    private byte[] getClassBytes(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        boolean eof = false;
        while (!eof) {
            try {
                int i = bis.read();
                if (i == -1)
                    eof = true;
                else baos.write(i);
            } catch (IOException e) {
                return null;
            }
        }
        return baos.toByteArray();
    }

    class ClassDefiner extends URLClassLoader{

        public ClassDefiner(URL[] urls) {
            super(urls);
        }

        public String getClassName(byte[] classBytes){
            return defineClass(null, classBytes, 0, classBytes.length).getName();
        }
    }
}

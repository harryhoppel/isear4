package org.spbgu.pmpu.athynia.worker.util;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: A.Selivanov
 * Date: 28.05.2007
 */
public class FileUtil {
    private static final Logger LOG = Logger.getLogger(FileUtil.class);

    public static void deleteFile(String filename) {
        File file = new File(filename);

        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception except) {
                except.printStackTrace();
            }
            if (file.exists()) {
                LOG.warn("WARNING:  Cannot delete file: " + file.getAbsolutePath());
            }
        }
    }
}

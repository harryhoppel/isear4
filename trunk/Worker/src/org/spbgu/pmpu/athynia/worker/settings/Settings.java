package org.spbgu.pmpu.athynia.worker.settings;

/**
 * Author: Selivanov
 * Date: 03.03.2007
 * Time: 14:03:31
 */
public interface Settings extends XMLSerializible {
    String QUEUE_FILE = "queue_file";

    Settings childSettings(String name);

    String getValue(String key);
    int getIntValue(String key);
    boolean getBoolValue(String key);
}

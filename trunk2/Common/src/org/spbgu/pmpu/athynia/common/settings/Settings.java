package org.spbgu.pmpu.athynia.common.settings;

/**
 * Author: Selivanov
 * Date: 03.03.2007
 * Time: 14:03:31
 */
public interface Settings extends XMLSerializible {
    String QUEUE_FILE = "queue_file";

    Settings childSettings(String name);

    String getValue(String key);
    String getValue(String key, String defaultValue);
    int getIntValue(String key);
    boolean getBoolValue(String key);
}

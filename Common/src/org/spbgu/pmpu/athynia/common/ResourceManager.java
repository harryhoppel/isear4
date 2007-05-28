package org.spbgu.pmpu.athynia.common;

/**
 * User: A.Selivanov
 * Date: 26.05.2007
 */
public interface ResourceManager {

    void commit();
    void abort();

    public void write(String key, String value);
    public void write(String key, String value, int currentPartNumber, int wholeNumbers, long timeoutUntilDrop);

    public JoinPart remove(String key);

    public int getSize();

    public JoinPart search(String key);
}

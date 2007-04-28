package org.spbgu.pmpu.athynia.worker.index.data;

/**
 * User: Pisar Vasiliy
 */
public interface DocumentUpdate {
    String getAddress();

    String getPlainText();

    String[] getWords();

    String[] getUniqueWords();
}

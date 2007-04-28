package org.spbgu.pmpu.athynia.worker.index;

import org.spbgu.pmpu.athynia.worker.index.data.DocumentUpdate;

/**
 * Author: Selivanov
 * Date: 03.03.2007
 * Time: 21:51:22
 */
public interface Index {
    //Strings only for first iteration, just like "hello world"
    void write(DocumentUpdate... docUpdates);

    String[] read(String keyWord);
}

package org.spbgu.pmpu.athynia.worker.index.helper;

import java.io.IOException;

/**
 * User: A.Selivanov
 * Date: 19.05.2007
 */
public abstract class TupleBrowser {

    public abstract boolean getNext(Tuple tuple) throws IOException;

    public abstract boolean getPrevious(Tuple tuple) throws IOException;
}

package org.spbgu.pmpu.athynia.central.communications.events;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: vasiliy
 */
public interface Saveable {
    void write(OutputStream toStream);

    void read(InputStream fromStream);
}

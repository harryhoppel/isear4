package org.spbgu.pmpu.athynia.worker.index.helper;

import java.io.IOException;
import java.io.Serializable;

/**
 * User: A.Selivanov
 * Date: 19.05.2007
 */
public interface Serializer extends Serializable {

    public byte[] serialize(Object obj) throws IOException;

    public Object deserialize(byte[] serialized) throws IOException;
}

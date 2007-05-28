package org.spbgu.pmpu.athynia.worker.resource;

import jdbm.helper.Serializer;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

import java.io.IOException;

/**
 * User: A.Selivanov
 * Date: 28.05.2007
 */
public class JoinPartSerializer implements Serializer {
    public static final String ENCODING_CHARSET = "UTF-8";

    public byte[] serialize(Object obj) throws IOException {
        return ((JoinPart) obj).toBinaryForm();
    }

    public Object deserialize(byte[] serialized) throws IOException {
        return new JoinPartImpl(serialized);
    }
}

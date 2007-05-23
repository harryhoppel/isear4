package org.spbgu.pmpu.athynia.worker.index;

import org.spbgu.pmpu.athynia.worker.index.helper.Serializer;

import java.io.IOException;

/**
 * User: A.Selivanov
 * Date: 19.05.2007
 */
public interface ResourseManager {
    static final int NAME_DIRECTORY_ROOT = 0;

    long insert(Object obj) throws IOException;

    long insert(Object obj, Serializer serializer) throws IOException;

    void delete(long recid) throws IOException;

    void update(long recid, Object obj) throws IOException;

    void update(long recid, Object obj, Serializer serializer) throws IOException;

    Object fetch(long recid) throws IOException;

    Object fetch(long recid, Serializer serializer) throws IOException;

    void close() throws IOException;

    int getRootCount();

    long getRoot(int id) throws IOException;

    void setRoot(int id, long rowid) throws IOException;

    void commit() throws IOException;

    void rollback() throws IOException;

    long getNamedObject(String name) throws IOException;

    void setNamedObject(String name, long recid) throws IOException;
}

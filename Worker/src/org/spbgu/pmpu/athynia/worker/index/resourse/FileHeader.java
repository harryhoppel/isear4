package org.spbgu.pmpu.athynia.worker.index.resourse;

/**
 * This class represents a file header. It is a 1:1 representation of
 * the data that appears in block 0 of a file.
 */
class FileHeader implements BlockView {
    // offsets
    private static final short O_MAGIC = 0; // short magic
    private static final short O_LISTS = Magic.SZ_SHORT; // long[2*NLISTS]
    private static final int O_ROOTS =
        O_LISTS + (Magic.NLISTS * 2 * Magic.SZ_LONG);

    // my block
    private BlockIo block;

    /**
     * The number of "root" rowids available in the file.
     */
    static final int NROOTS =
        (RecordFile.BLOCK_SIZE - O_ROOTS) / Magic.SZ_LONG;


    FileHeader(BlockIo block, boolean isNew) {
        this.block = block;
        if (isNew)
            block.writeShort(O_MAGIC, Magic.FILE_HEADER);
        else if (!magicOk())
            throw new Error("CRITICAL: file header magic not OK "
                + block.readShort(O_MAGIC));
    }

    /**
     * Returns true if the magic corresponds with the fileHeader magic.
     */
    private boolean magicOk() {
        return block.readShort(O_MAGIC) == Magic.FILE_HEADER;
    }


    /**
     * Returns the offset of the "first" block of the indicated list
     */
    private short offsetOfFirst(int list) {
        return (short) (O_LISTS + (2 * Magic.SZ_LONG * list));
    }

    /**
     * Returns the offset of the "last" block of the indicated list
     */
    private short offsetOfLast(int list) {
        return (short) (offsetOfFirst(list) + Magic.SZ_LONG);
    }

    /**
     * Returns the offset of the indicated root
     */
    private short offsetOfRoot(int root) {
        return (short) (O_ROOTS + (root * Magic.SZ_LONG));
    }

    /**
     * Returns the first block of the indicated list
     */
    long getFirstOf(int list) {
        return block.readLong(offsetOfFirst(list));
    }

    /**
     * Sets the first block of the indicated list
     */
    void setFirstOf(int list, long value) {
        block.writeLong(offsetOfFirst(list), value);
    }

    /**
     * Returns the last block of the indicated list
     */
    long getLastOf(int list) {
        return block.readLong(offsetOfLast(list));
    }

    /**
     * Sets the last block of the indicated list
     */
    void setLastOf(int list, long value) {
        block.writeLong(offsetOfLast(list), value);
    }

    /**
     * Returns the indicated root rowid. A root rowid is a special rowid
     * that needs to be kept between sessions. It could conceivably be
     * stored in a special file, but as a large amount of space in the
     * block header is wasted anyway, it's more useful to store it where
     * it belongs.
     *
     * @see #NROOTS
     */
    long getRoot(int root) {
        return block.readLong(offsetOfRoot(root));
    }

    /**
     * Sets the indicated root rowid.
     *
     * @see #getRoot
     * @see #NROOTS
     */
    void setRoot(int root, long rowid) {
        block.writeLong(offsetOfRoot(root), rowid);
    }
}

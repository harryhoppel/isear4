package org.spbgu.pmpu.athynia.worker.index;

/**
 * User: A.Selivanov
 * Date: 23.05.2007
 */
public class ResourseManagerOptions {
    /**
     * Option to create a thread-safe record manager.
     */
    public static final String PROVIDER_FACTORY = "org.spbgu.pmpu.athynia.worker.index.resourse.Provider";


    /**
     * Option to create a thread-safe record manager.
     */
    public static final String THREAD_SAFE = "org.spbgu.pmpu.athynia.worker.index.threadSafe";


    /**
     * Option to automatically commit data after each operation.
     */
    public static final String AUTO_COMMIT = "org.spbgu.pmpu.athynia.worker.index.autoCommit";


    /**
     * Option to disable transaction (to increase performance at the cost of
     * potential data loss).
     */
    public static final String DISABLE_TRANSACTIONS = "org.spbgu.pmpu.athynia.worker.index.disableTransactions";


    /**
     * Cache type.
     */
    public static final String CACHE_TYPE = "org.spbgu.pmpu.athynia.worker.index.cache.type";


    /**
     * Cache size (when applicable)
     */
    public static final String CACHE_SIZE = "org.spbgu.pmpu.athynia.worker.index.cache.size";


    /**
     * Use normal (strong) object references for the record cache.
     */
    public static final String NORMAL_CACHE = "normal";


    /**
     * Use soft references {$link java.lang.ref.SoftReference} for the record
     * cache instead of the default normal object references.
     * <p/>
     * Soft references are cleared at the discretion of the garbage collector
     * in response to memory demand.
     */
    public static final String SOFT_REF_CACHE = "soft";


    /**
     * Use weak references {$link java.lang.ref.WeakReference} for the record
     * cache instead of the default normal object references.
     * <p/>
     * Weak references do not prevent their referents from being made
     * finalizable, finalized, and then reclaimed.
     */
    public static final String WEAK_REF_CACHE = "weak";

}

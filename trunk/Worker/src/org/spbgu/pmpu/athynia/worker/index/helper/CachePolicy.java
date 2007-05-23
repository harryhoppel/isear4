package org.spbgu.pmpu.athynia.worker.index.helper;

import java.util.Enumeration;

/**
 * CachePolicity is an abstraction for different cache policies.
 * (ie. MRU, time-based, soft-refs, ...)
 *
 * @author <a href="mailto:boisvert@intalio.com">Alex Boisvert</a>
 * @author <a href="mailto:dranatunga@users.sourceforge.net">Dilum Ranatunga</a>
 * @version $Id: CachePolicy.java,v 1.5 2003/11/01 13:25:02 dranatunga Exp $
 */
public interface CachePolicy {

    /**
     * Place an object in the cache. If the cache does not currently contain
     * an object for the key specified, this mapping is added. If an object
     * currently exists under the specified key, the current object is
     * replaced with the new object.
     * <p/>
     * If the changes to the cache cause the eviction of any objects
     * <strong>stored under other key(s)</strong>, events corresponding to
     * the evictions are fired for each object. If an event listener is
     * unable to handle the eviction, and throws a cache eviction exception,
     * that exception is propagated to the caller. If such an exception is
     * thrown, the cache itself should be left as it was before the
     * <code>put()</code> operation was invoked: the the object whose
     * eviction failed is still in the cache, and the new insertion or
     * modification is reverted.
     *
     * @param key   key for the cached object
     * @param value the cached object
     * @throws CacheEvictionException propagated if, while evicting objects
     *                                to make room for new object, an eviction listener encountered
     *                                this problem.
     */
    public void put(Object key, Object value)
        throws CacheEvictionException;


    /**
     * Obtain the object stored under the key specified.
     *
     * @param key key the object was cached under
     * @return the object if it is still in the cache, null otherwise.
     */
    public Object get(Object key);


    /**
     * Remove the object stored under the key specified. Note that since
     * eviction notices are only fired when objects under <strong>different
     * keys</strong> are evicted, no event is fired for any object stored
     * under this key (see {@link #put(Object,Object) put( )}).
     *
     * @param key key the object was stored in the cache under.
     */
    public void remove(Object key);


    /**
     * Remove all objects from the cache. Consistent with
     * {@link #remove(Object) remove( )}, no eviction notices are fired.
     */
    public void removeAll();


    /**
     * Enumerate through the objects currently in the cache.
     */
    public Enumeration elements();


    /**
     * Add a listener to this cache policy.
     * <p/>
     * If this cache policy already contains a listener that is equal to
     * the one being added, this call has no effect.
     *
     * @param listener the (non-null) listener to add to this policy
     * @throws IllegalArgumentException if listener is null.
     */
    public void addListener(CachePolicyListener listener)
        throws IllegalArgumentException;


    /**
     * Remove a listener from this cache policy. The listener is found
     * using object equality, not identity.
     *
     * @param listener the listener to remove from this policy
     */
    public void removeListener(CachePolicyListener listener);

}

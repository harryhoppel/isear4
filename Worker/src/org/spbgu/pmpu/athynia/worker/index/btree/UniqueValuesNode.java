package org.spbgu.pmpu.athynia.worker.index.btree;

/**
 * User: vasiliy
 */
public interface UniqueValuesNode<K, V> {
    int getMaxSize();
    int getHalfSize();
    int getCurrentSize();
    boolean isFull();

    void split(UniqueValuesNode<K, V> original, UniqueValuesNode<K, V> child1, UniqueValuesNode<K, V> child2, K key, V value);

    boolean contains(K key);
    V getValue(K key);
    UniqueValuesNode<K, V> getNextNode(K key);

    void put(K key, V value);

    void delete(K key, V value);
    
    UniqueValuesNode<K, V> getParent();
    UniqueValuesNode<K, V>[] getChilds();

    V[] getValues();
    K[] getKeys();
}

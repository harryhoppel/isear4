package org.spbgu.pmpu.athynia.worker.index.btree.impl;

import org.spbgu.pmpu.athynia.worker.index.btree.UniqueValuesNode;

import java.util.Arrays;

/**
 * User: vasiliy
 */
public class UniqueValuesNodeImpl<K extends Comparable, V> implements UniqueValuesNode<K, V> {
    private final int maxSize;
    private final int halfSize;

    private final UniqueValuesNode<K, V> parent;

    private final Object[] keys;
    private final Object[] values;
    private int currentSize;

    private final UniqueValuesNode<K, V>[] childs;

    public UniqueValuesNodeImpl(int maxSize, int halfSize, UniqueValuesNode<K, V> parent) {
        this.maxSize = maxSize;
        this.halfSize = halfSize;
        this.parent = parent;
        this.currentSize = 0;
        keys = new Object[this.maxSize];
        values = new Object[this.maxSize];
        childs = new UniqueValuesNodeImpl[this.maxSize - 1];
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getHalfSize() {
        return halfSize;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public boolean isFull() {
        return currentSize == maxSize;
    }

    public void split(UniqueValuesNode<K, V> original, UniqueValuesNode<K, V> child1, UniqueValuesNode<K, V> child2, K key, V value) {
        for (int i = 0; i < childs.length; i++) {
            if (original.equals(childs[i])) {
                for (int j = i + 1; j < childs.length; j++) {
                    childs[j] = childs[j - 1];
                }
                childs[i] = child1;
                childs[i + 1] = child2;
                put(key, value);
                break;
            }
        }
    }

    public boolean contains(K key) {
        return Arrays.binarySearch(keys, key) >= 0;
    }

    public V getValue(K key) {
        int keyPosition = Arrays.binarySearch(keys, key);
        if (keyPosition >= 0) {
            return (V)values[keyPosition];
        } else {
            return null;
        }
    }

    public UniqueValuesNode<K, V> getNextNode(K key) {
        int keyPosition = Arrays.binarySearch(keys, key);
        if (keyPosition >= 0) {
            return this;
        } else {
            return childs[keyPosition];
        }
    }

    public void put(K key, V value) {
        if (currentSize == maxSize) {
            UniqueValuesNode<K, V> child1 = new UniqueValuesNodeImpl<K, V>(maxSize, halfSize, parent);
            UniqueValuesNode<K, V> child2 = new UniqueValuesNodeImpl<K, V>(maxSize, halfSize, parent);
            K keyToPop;
            V valueToPop;
            if (((K)keys[halfSize]).compareTo(key) < 0) {
                for (int i = 0; i < halfSize - 1; i++) {
                    child1.put((K)keys[i], (V)values[i]);
                }
                child1.put(key, value);
                for (int i = halfSize; i < maxSize; i++) {
                    child2.put((K)keys[i], (V)values[i]);
                }
                keyToPop = (K)keys[halfSize - 1];
                valueToPop = (V)values[halfSize - 1];
            } else {
                for (int i = 0; i < halfSize; i++) {
                    child1.put((K)keys[i], (V)values[i]);
                }

                for (int i = halfSize + 1; i < maxSize; i++) {
                    child2.put((K)keys[i], (V)values[i]);
                }
                child2.put(key, value);
                keyToPop = (K)keys[halfSize];
                valueToPop = (V)values[halfSize];
            }
            parent.split(this, child1, child2, keyToPop, valueToPop);
        } else {
            int insertPosition = Arrays.binarySearch(keys, key);
            if (insertPosition >= 0) {
                values[insertPosition] = value;
            } else {
                insertPosition = insertPosition * (-1) - 1;   //look javadocs for Arrays.binarySearch
                for (int i = maxSize; i > insertPosition; i--) {
                    keys[i] = keys[i - 1];
                    values[i] = values[i - 1];
                }
                keys[insertPosition] = key;
                values[insertPosition] = value;
            }
            currentSize++;
        }
    }

    public void delete(K key, V value) {
        throw new UnsupportedOperationException();
//        currentSize--;
    }

    public UniqueValuesNode<K, V> getParent() {
        return parent;
    }

    public UniqueValuesNode<K, V>[] getChilds() {
        return childs;
    }

    public V[] getValues() {
        return (V[]) values;
    }

    public K[] getKeys() {
        return (K[]) keys;
    }
}

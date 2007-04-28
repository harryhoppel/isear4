package org.spbgu.pmpu.athynia.worker.index.btree;

import org.spbgu.pmpu.athynia.worker.index.btree.impl.UniqueValuesNodeImpl;

/**
 * User: vasiliy
 */
public class UniqueValuesRoot<K extends Comparable, V> extends UniqueValuesNodeImpl<K, V> {
    private UniqueValuesNode<K, V> root;

    public UniqueValuesRoot(int maxSize, int halfSize, UniqueValuesNode<K, V> parent) {
        super(maxSize, halfSize, parent);
        root = this;
    }

    public void split(UniqueValuesNode<K, V> original, UniqueValuesNode<K, V> child1, UniqueValuesNode<K, V> child2, K key, V value) {
        if (isFull()) {
            UniqueValuesNode<K, V> previousRoot = root;
            root = new UniqueValuesRoot<K,V>(getMaxSize(), getHalfSize(), null);
            K[] keys = getKeys();
            V[] values = getValues();
            root.put(key, value);
            root.getChilds()[0] = new UniqueValuesNodeImpl<K,V>(getMaxSize(), getHalfSize(), root);
            root.getChilds()[1] = new UniqueValuesNodeImpl<K,V>(getMaxSize(), getHalfSize(), root);
            for (int i = 0; i < getHalfSize(); i++) {
                root.getChilds()[0].put(keys[i], values[i]);
                System.arraycopy(previousRoot.getChilds(), 0, root.getChilds()[0].getChilds(), 0, getHalfSize());
            }
            for (int i = getHalfSize(); i < getMaxSize(); i++) {
                root.getChilds()[1].put(keys[i], values[i]);
                System.arraycopy(previousRoot.getChilds(), getHalfSize(), root.getChilds()[0].getChilds(), 0, getHalfSize());
            }

        } else {
            super.split(original, child1, child2, key, value);
        }
    }

    public UniqueValuesNode<K, V> getRoot() {
        return root;
    }

    public UniqueValuesNode<K, V> getParent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

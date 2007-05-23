package org.spbgu.pmpu.athynia.worker.index.btree;

import org.spbgu.pmpu.athynia.worker.index.ResourseManager;
import org.spbgu.pmpu.athynia.worker.index.helper.Serializer;
import org.spbgu.pmpu.athynia.worker.index.helper.Tuple;
import org.spbgu.pmpu.athynia.worker.index.helper.TupleBrowser;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Comparator;


public class BTree implements Externalizable {
    private static final boolean DEBUG = false;
    final static long serialVersionUID = 1L;
    public static final int DEFAULT_SIZE = 16;
    protected transient ResourseManager _resman;
    private transient long _recid;
    protected Comparator _comparator;
    protected Serializer _keySerializer;
    protected Serializer _valueSerializer;
    private int _height;
    private transient long _root;
    protected int _pageSize;
    protected int _entries;
    private transient BPage _bpageSerializer;


    /**
     * No-argument constructor used by serialization.
     */
    public BTree() {
        // empty
    }

    public static BTree createInstance(ResourseManager recman,
                                       Comparator comparator)
        throws IOException {
        return createInstance(recman, comparator, null, null, DEFAULT_SIZE);
    }

    public static BTree createInstance(ResourseManager recman,
                                       Comparator comparator,
                                       Serializer keySerializer,
                                       Serializer valueSerializer)
        throws IOException {
        return createInstance(recman, comparator, keySerializer,
            valueSerializer, DEFAULT_SIZE);
    }

    public static BTree createInstance(ResourseManager recman,
                                       Comparator comparator,
                                       Serializer keySerializer,
                                       Serializer valueSerializer,
                                       int pageSize)
        throws IOException {
        BTree btree;

        if (recman == null) {
            throw new IllegalArgumentException("Argument 'resourse' is null");
        }

        if (comparator == null) {
            throw new IllegalArgumentException("Argument 'comparator' is null");
        }

        if (!(comparator instanceof Serializable)) {
            throw new IllegalArgumentException("Argument 'comparator' must be serializable");
        }

        if (keySerializer != null && !(keySerializer instanceof Serializable)) {
            throw new IllegalArgumentException("Argument 'keySerializer' must be serializable");
        }

        if (valueSerializer != null && !(valueSerializer instanceof Serializable)) {
            throw new IllegalArgumentException("Argument 'valueSerializer' must be serializable");
        }

        // make sure there's an even number of entries per BPage
        if ((pageSize & 1) != 0) {
            throw new IllegalArgumentException("Argument 'pageSize' must be even");
        }

        btree = new BTree();
        btree._resman = recman;
        btree._comparator = comparator;
        btree._keySerializer = keySerializer;
        btree._valueSerializer = valueSerializer;
        btree._pageSize = pageSize;
        btree._bpageSerializer = new BPage();
        btree._bpageSerializer._btree = btree;
        btree._recid = recman.insert(btree);
        return btree;
    }


    public static BTree load(ResourseManager recman, long recid)
        throws IOException {
        BTree btree = (BTree) recman.fetch(recid);
        btree._recid = recid;
        btree._resman = recman;
        btree._bpageSerializer = new BPage();
        btree._bpageSerializer._btree = btree;
        return btree;
    }

    public synchronized Object insert(Object key, Object value,
                                      boolean replace)
        throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Argument 'key' is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Argument 'value' is null");
        }

        BPage rootPage = getRoot();

        if (rootPage == null) {
            // BTree is currently empty, create a new root BPage
            if (DEBUG) {
                System.out.println("BTree.insert() new root BPage");
            }
            rootPage = new BPage(this, key, value);
            _root = rootPage._recid;
            _height = 1;
            _entries = 1;
            _resman.update(_recid, this);
            return null;
        } else {
            BPage.InsertResult insert = rootPage.insert(_height, key, value, replace);
            boolean dirty = false;
            if (insert._overflow != null) {
                // current root page overflowed, we replace with a new root page
                if (DEBUG) {
                    System.out.println("BTree.insert() replace root BPage due to overflow");
                }
                rootPage = new BPage(this, rootPage, insert._overflow);
                _root = rootPage._recid;
                _height += 1;
                dirty = true;
            }
            if (insert._existing == null) {
                _entries++;
                dirty = true;
            }
            if (dirty) {
                _resman.update(_recid, this);
            }
            // insert might have returned an existing value
            return insert._existing;
        }
    }

    public synchronized Object remove(Object key)
        throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Argument 'key' is null");
        }

        BPage rootPage = getRoot();
        if (rootPage == null) {
            return null;
        }
        boolean dirty = false;
        BPage.RemoveResult remove = rootPage.remove(_height, key);
        if (remove._underflow && rootPage.isEmpty()) {
            _height -= 1;
            dirty = true;

            // TODO:  check contract for BPages to be removed from resourse.
            if (_height == 0) {
                _root = 0;
            } else {
                _root = rootPage.childBPage(_pageSize - 1)._recid;
            }
        }
        if (remove._value != null) {
            _entries--;
            dirty = true;
        }
        if (dirty) {
            _resman.update(_recid, this);
        }
        return remove._value;
    }

    public synchronized Object find(Object key)
        throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Argument 'key' is null");
        }
        BPage rootPage = getRoot();
        if (rootPage == null) {
            return null;
        }

        Tuple tuple = new Tuple(null, null);
        TupleBrowser browser = rootPage.find(_height, key);

        if (browser.getNext(tuple)) {
            // find returns the matching key or the next ordered key, so we must
            // check if we have an exact match
            if (_comparator.compare(key, tuple.getKey()) != 0) {
                return null;
            } else {
                return tuple.getValue();
            }
        } else {
            return null;
        }
    }

    public synchronized Tuple findGreaterOrEqual(Object key)
        throws IOException {
        Tuple tuple;
        TupleBrowser browser;

        if (key == null) {
            // there can't be a key greater than or equal to "null"
            // because null is considered an infinite key.
            return null;
        }

        tuple = new Tuple(null, null);
        browser = browse(key);
        if (browser.getNext(tuple)) {
            return tuple;
        } else {
            return null;
        }
    }

    public synchronized TupleBrowser browse()
        throws IOException {
        BPage rootPage = getRoot();
        if (rootPage == null) {
            return EmptyBrowser.INSTANCE;
        }
        return rootPage.findFirst();
    }

    public synchronized TupleBrowser browse(Object key)
        throws IOException {
        BPage rootPage = getRoot();
        if (rootPage == null) {
            return EmptyBrowser.INSTANCE;
        }
        return rootPage.find(_height, key);
    }


    public synchronized int size() {
        return _entries;
    }

    public long getRecid() {
        return _recid;
    }

    private BPage getRoot()
        throws IOException {
        if (_root == 0) {
            return null;
        }
        BPage root = (BPage) _resman.fetch(_root, _bpageSerializer);
        root._recid = _root;
        root._btree = this;
        return root;
    }


    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {
        _comparator = (Comparator) in.readObject();
        _keySerializer = (Serializer) in.readObject();
        _valueSerializer = (Serializer) in.readObject();
        _height = in.readInt();
        _root = in.readLong();
        _pageSize = in.readInt();
        _entries = in.readInt();
    }

    public void writeExternal(ObjectOutput out)
        throws IOException {
        out.writeObject(_comparator);
        out.writeObject(_keySerializer);
        out.writeObject(_valueSerializer);
        out.writeInt(_height);
        out.writeLong(_root);
        out.writeInt(_pageSize);
        out.writeInt(_entries);
    }

    static class EmptyBrowser
        extends TupleBrowser {

        static TupleBrowser INSTANCE = new EmptyBrowser();

        public boolean getNext(Tuple tuple) {
            return false;
        }

        public boolean getPrevious(Tuple tuple) {
            return false;
        }
    }
}


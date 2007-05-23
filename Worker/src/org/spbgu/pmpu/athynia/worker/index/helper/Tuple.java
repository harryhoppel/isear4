package org.spbgu.pmpu.athynia.worker.index.helper;

/**
 * User: A.Selivanov
 * Date: 19.05.2007
 */
public class Tuple {
    private Object _key;
    private Object _value;

    public Tuple() {
        // empty
    }

    public Tuple(Object key, Object value) {
        _key = key;
        _value = value;
    }

    public Object getKey() {
        return _key;
    }

    public void setKey(Object key) {
        _key = key;
    }

    public Object getValue() {
        return _value;
    }

    public void setValue(Object value) {
        _value = value;
    }
}

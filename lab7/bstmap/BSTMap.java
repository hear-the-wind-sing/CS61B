package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<Key extends Comparable<Key>, Value> implements Map61B<Key, Value> {
    private class BSTNode {
        private BSTNode left;
        private BSTNode right;
        private Key key;
        private Value value;
        BSTNode(BSTNode l, BSTNode r, Key k, Value v) {
            left = l;
            right = r;
            key = k;
            value = v;
        }
        public void put(Key putK, Value putV) {
            if (key.compareTo(putK) > 0) {
                if (left == null) {
                    left = new BSTNode(null, null, putK, putV);
                    return;
                }
                this.left.put(putK, putV);
            } else {
                if (right == null) {
                    right = new BSTNode(null, null, putK, putV);
                    return;
                }
                this.right.put(putK, putV);
            }
        }
        public Value get(Key getK) {
            if (key.equals(getK)) {
                return value;
            } else if (key.compareTo(getK) > 0) {
                if (this.left == null) {
                    return null;
                }
                return this.left.get(getK);
            } else {
                if (this.right == null) {
                    return null;
                }
                return this.right.get(getK);
            }
        }
        public boolean contains(Key conK) {
            if (key.equals(conK)) {
                return true;
            } else if (key.compareTo(conK) > 0) {
                if (this.left == null) {
                    return false;
                }
                return this.left.contains(conK);
            } else {
                if (this.right == null) {
                    return false;
                }
                return this.right.contains(conK);
            }
        }
    }
    private BSTNode root;
    private int size;
    public void clear() {
        root = null;
        size = 0;
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public boolean containsKey(Key key) {
        if (root == null) {
            return false;
        }
        return root.contains(key);
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Value get(Key key) {
        if (root == null) {
            return null;
        }
        return root.get(key);
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public int size() {
        return size;
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public void put(Key key, Value value) {
        size++;
        if (root == null) {
            root = new BSTNode(null, null, key, value);
            return;
        }
        root.put(key, value);
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Set<Key> keySet() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Value remove(Key key) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Value remove(Key key, Value value) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Iterator<Key> iterator() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
}

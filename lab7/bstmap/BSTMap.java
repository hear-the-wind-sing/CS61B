package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        private BSTNode left;
        private BSTNode right;
        private K key;
        private V value;
        BSTNode(BSTNode l, BSTNode r, K k, V v) {
            left = l;
            right = r;
            key = k;
            value = v;
        }
        public void put(K putK, V putV) {
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
        public V get(K getK) {
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
        public boolean contains(K conK) {
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
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.contains(key);
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public V get(K key) {
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
    public void put(K key, V value) {
        size++;
        if (root == null) {
            root = new BSTNode(null, null, key, value);
            return;
        }
        root.put(key, value);
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Set<K> keySet() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public V remove(K key) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public void printInOrder() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
}

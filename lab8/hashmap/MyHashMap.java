package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size = 0;

    private int INITIALSIZE = 16;
    private double LOADFACTOR = 0.75;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(INITIALSIZE);
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        LOADFACTOR = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
        //        return null;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
        //return null;
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] arrayOfCollections = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            arrayOfCollections[i] = createBucket();
        }
        return arrayOfCollections;
        //return null;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    public void clear() {
        size = 0;
        buckets = createTable(INITIALSIZE);
        return;
    }
    public boolean containsKey(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for(Node node : buckets[index]) {
            if(node.key.equals(key)) {
                return true;
            }
        }
        return false;
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public V get(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for(Node node : buckets[index]) {
            if(node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public int size() {
        return size;
    }
    public void put(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
               node.value = value;
               return;
            }
        }
        buckets[index].add(createNode(key, value));
        size++;
        if ((double)(size / buckets.length) > LOADFACTOR) {
            resize(buckets.length * 2);
        }
//        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public void resize(int  capacity) {
        Set<Node> nodeSet = nodeSet();
        buckets = createTable(capacity);
        size = 0;
        for(Node node: nodeSet){
            put(node.key,node.value);
        }
    }
    public Set<Node> nodeSet() {
        HashSet<Node> nodeSet = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            for(Node node : buckets[i]){
                nodeSet.add(node);
            }
        }
        return nodeSet;
    }
    public Set<K> keySet() {
        HashSet<K> kSet = new HashSet<>();
        for (int i = 0; i < buckets.length; i++) {
            for(Node node : buckets[i]){
                kSet.add(node.key);
            }
        }
        return kSet;
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
    public V remove(K key) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
    public Iterator<K> iterator(){
        return keySet().iterator();
//        return new MyHashMapIterator();
        //throw new UnsupportedOperationException("This operation is not supported.");
    }
//    private class MyHashMapIterator implements Iterator<K> {
//        private Iterator<K> iterator = buckets[0].iterator();
//        public boolean hasNext(){
//
//        }
//        public K next(){
//
//        }
//    }
}

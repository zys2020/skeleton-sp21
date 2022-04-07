package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author Yuansong Zhang
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        public Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /**
     * Instance Variables
     */
    private Collection<Node>[] buckets;

    private int size;

    private int initialSize;

    private double maxLoad;

    /**
     * Constructors
     */
    public MyHashMap() {
        this(16);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        this.buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        this.size = 0;
        this.buckets = this.createTable(this.initialSize);
    }

    @Override
    public boolean containsKey(K key) {
        int hashIndex = convertToHashIndex(key.hashCode());
        Collection<Node> bucket = this.buckets[hashIndex];
        if (bucket == null) {
            return false;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    private int convertToHashIndex(int hashCode) {
        return Math.floorMod(hashCode, this.buckets.length);
    }

    @Override
    public V get(K key) {
        int hashIndex = convertToHashIndex(key.hashCode());
        Collection<Node> bucket = this.buckets[hashIndex];
        if (bucket == null) {
            return null;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        int hashIndex = convertToHashIndex(key.hashCode());
        Collection<Node> bucket = this.buckets[hashIndex];
        Collection<Node> newBucket = this.createBucket();
        newBucket.add(this.createNode(key, value));
        if (bucket != null) {
            for (Node node : bucket) {
                if (node.key.equals(key)) {
                    this.size -= 1;
                    continue;
                }
                newBucket.add(node);
            }
        }
        this.buckets[hashIndex] = newBucket;
        this.size += 1;
        if ((double) (this.size / this.buckets.length) > this.maxLoad) {
            this.resizeBuckets();
        }
    }

    private void resizeBuckets() {
        Collection<Node>[] oldBuckets = this.buckets;
        Collection<Node>[] newBuckets = this.createTable(this.size * 2);
        this.buckets = newBuckets;
        for (Collection<Node> bucket : oldBuckets) {
            if (bucket == null) {
                continue;
            }
            for (Node node : bucket) {
                int hashIndex = this.convertToHashIndex(node.key.hashCode());
                if (newBuckets[hashIndex] == null) {
                    newBuckets[hashIndex] = this.createBucket();
                }
                newBuckets[hashIndex].add(node);
            }
        }
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        for (Collection<Node> bucket : this.buckets) {
            if (bucket == null) {
                continue;
            }
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            return null;
        }
        return this.remove(key, this.get(key));
    }

    @Override
    public V remove(K key, V value) {
        if (!this.containsKey(key) || this.get(key) != value) {
            return null;
        }
        int hashIndex = convertToHashIndex(key.hashCode());
        Collection<Node> bucket = this.buckets[hashIndex];
        Collection<Node> newBucket = this.createBucket();
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                this.size -= 1;
                continue;
            }
            newBucket.add(node);
        }
        this.buckets[hashIndex] = newBucket;
        return value;

    }

    @Override
    public Iterator<K> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<K> {
        int bucketIndex;
        Collection<Node> bucket;

        public MyIterator() {
            this.bucketIndex = 0;
            this.bucket = createBucket();
            this.bucket.addAll(buckets[this.bucketIndex]);
        }

        @Override
        public boolean hasNext() {
            if (this.bucketIndex >= buckets.length) {
                return false;
            } else return this.bucket.isEmpty();
        }

        @Override
        public K next() {
            if (this.bucket.isEmpty()) {
                this.bucketIndex += 1;
                while (buckets[this.bucketIndex].isEmpty()) {
                    this.bucketIndex += 1;
                    this.bucket.addAll(buckets[this.bucketIndex]);
                }
            }
            for (Node node : this.bucket) {
                this.bucket.remove(node);
                return node.key;
            }
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello");
    }
}

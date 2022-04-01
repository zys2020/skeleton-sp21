package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    BSTNode rootNode;

    private class BSTNode {
        K key;
        V value;
        BSTNode leftNode;
        BSTNode rightNode;
        int size;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.leftNode = null;
            this.rightNode = null;
            this.size = 1;
        }
    }

    public BSTMap() {
        this.rootNode = null;
    }

    /**
     * Removes all the mappings from this map.
     */
    @Override
    public void clear() {
        this.rootNode = null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(K key) {
        BSTNode node = get(this.rootNode, key);
        return node != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        BSTNode node = get(this.rootNode, key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    private BSTNode get(BSTNode rootNode, K key) {
        if (rootNode == null) {
            return null;
        }
        if (rootNode.key.compareTo(key) == 0) {
            return rootNode;
        } else if (rootNode.key.compareTo(key) > 0) {
            return get(rootNode.leftNode, key);
        } else {
            return get(rootNode.rightNode, key);
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        if (this.rootNode == null) {
            return 0;
        }
        return this.rootNode.size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     */
    @Override
    public void put(K key, V value) {
        this.rootNode = put(this.rootNode, key, value);
    }

    private BSTNode put(BSTNode rootNode, K key, V value) {
        if (rootNode == null) {
            return new BSTNode(key, value);
        }
        if (rootNode.key.compareTo(key) > 0) {
            rootNode.leftNode = put(rootNode.leftNode, key, value);
        } else if (rootNode.key.compareTo(key) < 0) {
            rootNode.rightNode = put(rootNode.rightNode, key, value);
        }
        int leftSize = 0;
        int rightSize = 0;
        if (rootNode.leftNode != null) {
            leftSize = rootNode.leftNode.size;
        }
        if (rootNode.rightNode != null) {
            rightSize = rootNode.rightNode.size;
        }
        rootNode.size = 1 + leftSize + rightSize;
        return rootNode;
    }

    /**
     * Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException.
     */
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        keySet(this.rootNode, set);
        return set;
    }

    private void keySet(BSTNode rootNode, Set<K> set) {
        if (rootNode == null) {
            return;
        }
        keySet(rootNode.leftNode, set);
        keySet(rootNode.rightNode, set);
        set.add(rootNode.key);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        V value = get(key);
        this.rootNode = remove(this.rootNode, key);
        return value;
    }

    private BSTNode remove(BSTNode rootNode, K key) {
        if (rootNode == null) {
            return null;
        }
        if (rootNode.key.compareTo(key) > 0) {
            rootNode.leftNode = remove(rootNode.leftNode, key);
        } else if (rootNode.key.compareTo(key) < 0) {
            rootNode.rightNode = remove(rootNode.rightNode, key);
        } else {
            if (rootNode.leftNode == null && rootNode.rightNode == null) {
                return null;
            } else if (rootNode.leftNode == null) {
                rootNode = rootNode.rightNode;
            } else if (rootNode.rightNode == null) {
                rootNode = rootNode.leftNode;
            } else {
                BSTNode minNode = findMin(rootNode.rightNode);
                minNode.rightNode = removeMin(rootNode.rightNode);
                minNode.leftNode = rootNode.leftNode;
                rootNode = minNode;
            }
        }
        int leftSize = 0;
        int rightSize = 0;
        if (rootNode.leftNode != null) {
            leftSize = rootNode.leftNode.size;
        }
        if (rootNode.rightNode != null) {
            rightSize = rootNode.rightNode.size;
        }
        rootNode.size = 1 + leftSize + rightSize;
        return rootNode;
    }

    private BSTNode removeMin(BSTNode rootNode) {
        if (rootNode.leftNode == null) {
            return rootNode.rightNode;
        }
        rootNode.leftNode = removeMin(rootNode.leftNode);
        return rootNode;
    }

    private BSTNode findMin(BSTNode rootNode) {
        if (rootNode.leftNode == null) {
            return rootNode;
        }
        return findMin(rootNode.leftNode);
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        if (get(key) != value) {
            return null;
        }
        remove(key);
        return value;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator(this);
    }

    private class BSTMapIterator implements Iterator<K> {
        BSTMap<K, V> map;

        public BSTMapIterator(BSTMap<K, V> map) {
            this.map = map;
        }

        @Override
        public boolean hasNext() {
            return map.rootNode != null;
        }

        @Override
        public K next() {
            BSTNode node = map.findMin(map.rootNode);
            map.rootNode = map.removeMin(map.rootNode);
            return node.key;
        }
    }

    /**
     * Print the BSTMap in order of increasing keys.
     */
    public void printInOrder() {
        printInOrder(this.rootNode);
    }

    private void printInOrder(BSTNode rootNode) {
        if (rootNode == null) {
            return;
        }
        printInOrder(rootNode.leftNode);
        System.out.print(rootNode.key + ", ");
        printInOrder(rootNode.rightNode);
    }

    public static void main(String[] args) {
        BSTMap<String, String> map = new BSTMap<>();
        for (int i = 1; i < 27; i++) {
            String key = String.valueOf((char) (96 + i));
            String value = String.valueOf(i);
            map.put(key, value);
        }
        map.printInOrder();
    }
}

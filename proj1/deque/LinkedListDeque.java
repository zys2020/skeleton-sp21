package deque;

import java.util.Iterator;

/**
 * An implementation of deque using circular sentinel topology.
 */
public class LinkedListDeque<Item> implements Deque<Item> {
    private class Node {
        public Item item;
        public Node prev;
        public Node next;

        public Node(Item item) {
            this.item = item;
        }
    }

    /**
     * The first node is always the next node of sentinelNode and the last node is always the prev node of sentinelNode.
     */
    private final Node sentinelNode;
    private int size;

    /**
     * Constructor
     */
    public LinkedListDeque() {
        sentinelNode = new Node(null);
        sentinelNode.next = sentinelNode;
        sentinelNode.prev = sentinelNode;
        size = 0;
    }

    /**
     * Adds an item of type Item to the front of the deque. You can assume that item is never null.
     * A single such operation must take “constant time”.
     */
    @Override
    public void addFirst(Item item) {
        Node node = new Node(item);
        node.next = sentinelNode.next;
        node.prev = sentinelNode;
        sentinelNode.next.prev = node;
        sentinelNode.next = node;
        size += 1;
    }

    /**
     * Adds an item of type Item to the back of the deque. You can assume that item is never null.
     * A single such operation must take “constant time”.
     */
    @Override
    public void addLast(Item item) {
        Node node = new Node(item);
        node.next = sentinelNode;
        node.prev = sentinelNode.prev;
        sentinelNode.prev.next = node;
        sentinelNode.prev = node;
        size += 1;
    }

    /**
     * Returns the number of items in the deque.
     * Take constant time.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
        Node node = sentinelNode.next;
        for (int i = 0; i < size; i++) {
            if (node == null) {
                System.out.println();
                return;
            } else {
                System.out.print(node.item + " ");
                node = node.next;
            }
        }
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     * A single such operation must take “constant time”.
     */
    @Override
    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        Node node = sentinelNode.next;
        sentinelNode.next = sentinelNode.next.next;
        sentinelNode.next.prev = sentinelNode;
        size -= 1;
        return node.item;
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     * A single such operation must take “constant time”.
     */
    @Override
    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        Node node = sentinelNode.prev;
        sentinelNode.prev = sentinelNode.prev.prev;
        sentinelNode.prev.next = sentinelNode;
        size -= 1;
        return node.item;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     * Must use iteration, not recursion.
     */
    @Override
    public Item get(int index) {
        Node node = sentinelNode.next;
        for (int i = 0; i < size; i++) {
            if (i == index) {
                return node.item;
            } else {
                node = node.next;
            }
        }
        return null;
    }

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<Item>)
     * so we must provide this method to return an iterator.
     * Iterating over the LinkedListDeque using a for-each loop should take time proportional to the number of items.
     */
    @Override
    public Iterator<Item> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<Item> {
        private Node node;

        public LinkedListDequeIterator() {
            node = sentinelNode;
        }

        @Override
        public boolean hasNext() {
            return this.node.next != sentinelNode;
        }

        @Override
        public Item next() {
            Item item = node.next.item;
            node = node.next;
            return item;
        }
    }

    /**
     * Returns whether the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as governed by the generic T’s equals method) in the same order
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        } else if (size() != ((Deque<?>) o).size()) {
            return false;
        } else {
            Iterator<Item> iter1 = iterator();
            Iterator<Item> iter2 = ((Deque<Item>) o).iterator();
            while (iter1.hasNext()) {
                Item a = iter1.next();
                Item b = iter2.next();
                if (!a.equals(b)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * It is the same as get, but uses recursion.
     */
    public Item getRecursive(int index) {
        return getNode(index, sentinelNode).item;
    }

    /**
     * Return the index th node from a node.
     */
    private Node getNode(int index, Node node) {
        if (index == 0) {
            return node.next;
        }
        return getNode(index - 1, node.next);
    }

}

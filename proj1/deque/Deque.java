package deque;

import java.util.Iterator;

/**
 * Deque is the abbreviation for Double Ended Queue.
 */
public interface Deque<Item> {
    /**
     * Adds an item of type Item to the front of the deque. You can assume that item is never null.
     */
    public void addFirst(Item item);

    /**
     * Adds an item of type Item to the back of the deque. You can assume that item is never null.
     */
    public void addLast(Item item);

    /**
     * Returns true if deque is empty, false otherwise.
     */
    public boolean isEmpty();

    /**
     * Returns the number of items in the deque.
     */
    public int size();

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    public void printDeque();

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    public Item removeFirst();

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    public Item removeLast();


    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    public Item get(int index);
//
//    /**
//     * The Deque objects we’ll make are iterable (i.e. Iterable<Item>)
//     * so we must provide this method to return an iterator.
//     */
//    public Iterator<Item> iterator();
//
//    /**
//     * Returns whether the parameter o is equal to the Deque.
//     * o is considered equal if it is a Deque and if it contains the same contents
//     * (as governed by the generic T’s equals method) in the same order
//     */
//    public boolean equals(Object o);

}

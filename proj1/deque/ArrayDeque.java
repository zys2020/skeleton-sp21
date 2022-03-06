package deque;

import java.util.Iterator;

/**
 * An implementation of deque using array as the core data structure.
 */
public class ArrayDeque<Item> implements Deque<Item> {
    private Item[] values;
    // The first item equals values[firstIndex]
    private int firstIndex;
    // The last item equals values[lastIndex - 1]
    private int lastIndex;
    private int size;
    private static float minUsageRatio;
    private static float maxUsageRatio;

    public ArrayDeque() {
        firstIndex = 0;
        lastIndex = 0;
        size = 0;
        int initSize = 8; // The starting size of your array should be 8.
        values = (Item[]) new Object[initSize];
        minUsageRatio = 0.25F; // For arrays of length 16 or more, your usage factor should always be at least 25%.
        maxUsageRatio = 0.50F;
    }

    /**
     * Adds an item of type Item to the front of the deque. You can assume that item is never null.
     * Take constant time, except during resizing operations.
     */
    @Override
    public void addFirst(Item item) {
        if (size() == values.length) {
            increaseSize();
            firstIndex = values.length - 1;
        } else if (firstIndex <= lastIndex) {
            if (firstIndex > 0) {
                firstIndex -= 1;
            } else {
                firstIndex = values.length - 1;
            }
        } else {
            firstIndex -= 1;
        }
        values[firstIndex] = item;
        size += 1;
    }

    /**
     * Replace the original array with a larger array.
     */
    private void increaseSize() {
        Item[] new_values = (Item[]) new Object[(int) (values.length / maxUsageRatio)];
        if (firstIndex < lastIndex) {
            System.arraycopy(values, 0, new_values, 0, values.length);
        } else {
            System.arraycopy(values, firstIndex, new_values, 0, values.length - firstIndex);
            System.arraycopy(values, 0, new_values, values.length - firstIndex, lastIndex);
            firstIndex = 0;
            lastIndex = values.length;
        }
        values = new_values;
    }

    /**
     * Adds an item of type Item to the back of the deque. You can assume that item is never null.
     * Take constant time, except during resizing operations.
     */
    @Override
    public void addLast(Item item) {
        if (size() == values.length) {
            increaseSize();
            lastIndex += 1;
        } else if (firstIndex <= lastIndex) {
            if (firstIndex < values.length) {
                lastIndex += 1;
            } else {
                lastIndex = 0;
            }
        } else {
            lastIndex += 1;
        }
        values[lastIndex - 1] = item;
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
     * Return true if deque is full, false otherwise.
     */
    public boolean isFull() {
        return size() == values.length;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
        if (firstIndex <= lastIndex) {
            for (int i = firstIndex; i < lastIndex; i++) {
                System.out.print(values[i] + " ");
            }
        } else {
            for (int i = firstIndex; i < values.length; i++) {
                System.out.print(values[i] + " ");
            }
            for (int i = 0; i < lastIndex; i++) {
                System.out.print(values[i] + " ");
            }
        }
        System.out.println();
    }

    /**
     * Return true if the usage ratio is less than the minimal usage ratio.
     */
    public boolean isLowUsageRatio() {
        return ((float) size() / values.length) < minUsageRatio;
    }

    /**
     * Replace the original array with a smaller array.
     */
    private void decreaseSize() {
        if (size() <= 16) {
            return;
        }
        int length = (int) (size() / maxUsageRatio);
        Item[] new_values = (Item[]) new Object[length];
        if (firstIndex < lastIndex) {
            System.arraycopy(values, firstIndex, new_values, 0, size());
            firstIndex = 0;
            lastIndex = size();
        } else {
            System.arraycopy(values, firstIndex - 1, new_values, 0, values.length - firstIndex);
            System.arraycopy(values, 0, new_values, values.length - firstIndex, lastIndex);
            firstIndex = 0;
            lastIndex = values.length;
        }
        values = new_values;
    }

    /**
     * Return and remove the first item of the deque under the normal condition.
     */
    private Item removeFirstBase() {
        if (firstIndex < lastIndex) {
            Item item = values[firstIndex];
            values[firstIndex] = null;
            firstIndex += 1;
            size -= 1;
            return item;
        } else {
            if (firstIndex == values.length - 1) {
                Item item = values[firstIndex];
                values[firstIndex] = null;
                firstIndex = 0;
                size -= 1;
                return item;
            } else if (firstIndex < values.length - 1) {
                Item item = values[firstIndex];
                values[firstIndex] = null;
                firstIndex += 1;
                size -= 1;
                return item;
            } else {
                System.out.println("The bug of firstIndex occurs.");
                return null;
            }
        }
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     * Take constant time, except during resizing operations.
     */
    @Override
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        } else if (isLowUsageRatio()) {
            decreaseSize();
        }
        return removeFirstBase();
    }

    /**
     * Return and remove the last item of the deque under the normal condition.
     */
    private Item removeLastBase() {
        if (firstIndex < lastIndex) {
            Item item = values[lastIndex - 1];
            values[lastIndex - 1] = null;
            lastIndex -= 1;
            size -= 1;
            return item;
        } else {
            if (lastIndex == 0) {
                Item item = values[values.length - 1];
                values[values.length - 1] = null;
                lastIndex = values.length - 1;
                size -= 1;
                return item;
            } else if (lastIndex > 0) {
                Item item = values[lastIndex - 1];
                values[lastIndex - 1] = null;
                lastIndex -= 1;
                size -= 1;
                return item;
            } else {
                System.out.println("The bug of lastIndex occurs.");
                return null;
            }
        }
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     * Take constant time, except during resizing operations.
     */
    @Override
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        } else if (isLowUsageRatio()) {
            decreaseSize();
        }
        return removeLastBase();
    }


    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     * Take constant time.
     */
    @Override
    public Item get(int index) {
        if (isEmpty()) {
            return null;
        } else {
            return values[(firstIndex + index) % values.length];
        }
    }

    /**
     * The Deque objects we’ll make are iterable (i.e. Iterable<Item>)
     * so we must provide this method to return an iterator.
     */
    @Override
    public Iterator<Item> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<Item> {
        private int n;

        public ArrayDequeIterator() {
            n = 0;
        }

        @Override
        public boolean hasNext() {
            return n < size();
        }

        @Override
        public Item next() {
            Item item = values[n];
            n += 1;
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

}

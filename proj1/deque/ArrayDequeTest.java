package deque;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    /**
     * Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     */
    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    /**
     * Adds an item, then removes an item, and ensures that dll is empty afterwards.
     */
    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.addFirst(100);
        lld1.addFirst(1000);
        assertEquals(3, lld1.size());

        assertEquals(Integer.valueOf(1000), lld1.removeFirst());
        assertEquals(Integer.valueOf(10), lld1.removeLast());
        assertEquals(Integer.valueOf(100), lld1.removeLast());

        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    /**
     * Tests removing from an empty deque
     */
    @Test
    public void removeEmptyTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    /**
     * Check if you can create ArrayDeque with different parameterized types.
     */
    @Test
    public void multipleParamTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();
        ArrayDeque<Double> lld2 = new ArrayDeque<>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    /**
     * check if null is return when removing from an empty LinkedListDeque.
     */
    @Test
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertNull("Should return null when removeFirst is called on an empty Deque,", lld1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,", lld1.removeLast());
    }

    /**
     * Add large number of elements to deque; check if order is correct.
     */
    @Test
    public void bigLLDequeTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    /**
     * Check if the difference between maximal elapsed time and minimal elapsed time is greater than 1s.
     * if false, it represents that the execution time of add and remove is constant.
     */
    @Test
    public void addAndRemoveTimeTest() {
        ArrayDeque<Integer> lld = new ArrayDeque<>();
        Stopwatch stopwatch = new Stopwatch();
        lld.addLast(0);
        double elapsedTime = stopwatch.elapsedTime();
        double minElapsedTime = elapsedTime;
        double maxElapsedTime = elapsedTime;
        for (int i = 1; i < 1000000; i++) {
            if (lld.isFull()) {
                lld.addLast(i);
                continue;
            }
            stopwatch = new Stopwatch();
            lld.addLast(i);
            elapsedTime = stopwatch.elapsedTime();
            maxElapsedTime = Math.max(maxElapsedTime, elapsedTime);
            minElapsedTime = Math.min(minElapsedTime, elapsedTime);
        }
        assertTrue("The difference between maxElapsedTime and minElapsedTime is greater than 1s",
                maxElapsedTime - minElapsedTime < 1);

        lld.removeLast();
        elapsedTime = stopwatch.elapsedTime();
        maxElapsedTime = elapsedTime;
        minElapsedTime = elapsedTime;
        for (int i = 1; i < 1000000; i++) {
            if (lld.isLowUsageRatio()) {
                lld.removeLast();
                continue;
            }
            stopwatch = new Stopwatch();
            lld.removeLast();
            elapsedTime = stopwatch.elapsedTime();
            maxElapsedTime = Math.max(maxElapsedTime, elapsedTime);
            minElapsedTime = Math.min(minElapsedTime, elapsedTime);
        }
        assertTrue("The difference between maxElapsedTime and minElapsedTime is greater than 1s",
                maxElapsedTime - minElapsedTime < 1);

        lld.addFirst(0);
        elapsedTime = stopwatch.elapsedTime();
        minElapsedTime = elapsedTime;
        maxElapsedTime = elapsedTime;
        for (int i = 1; i < 1000000; i++) {
            if (lld.isFull()) {
                lld.addFirst(i);
                continue;
            }
            stopwatch = new Stopwatch();
            lld.addFirst(i);
            elapsedTime = stopwatch.elapsedTime();
            maxElapsedTime = Math.max(maxElapsedTime, elapsedTime);
            minElapsedTime = Math.min(minElapsedTime, elapsedTime);
        }
        assertTrue("The difference between maxElapsedTime and minElapsedTime is greater than 1s",
                maxElapsedTime - minElapsedTime < 1);

        lld.removeFirst();
        elapsedTime = stopwatch.elapsedTime();
        maxElapsedTime = elapsedTime;
        minElapsedTime = elapsedTime;
        for (int i = 1; i < 1000000; i++) {
            if (lld.isLowUsageRatio()) {
                lld.removeFirst();
                continue;
            }
            stopwatch = new Stopwatch();
            lld.removeFirst();
            elapsedTime = stopwatch.elapsedTime();
            maxElapsedTime = Math.max(maxElapsedTime, elapsedTime);
            minElapsedTime = Math.min(minElapsedTime, elapsedTime);
        }
        assertTrue("The difference between maxElapsedTime and minElapsedTime is greater than 1s",
                maxElapsedTime - minElapsedTime < 1);
    }

    @Test
    public void getTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        for (int i = 0; i < 10000; i++) {
            lld.addLast(i);
        }
        assertEquals(Integer.valueOf(10), lld.get(10));
        assertEquals(Integer.valueOf(220), lld.get(220));
        assertEquals(Integer.valueOf(5000), lld.get(5000));
        assertEquals(Integer.valueOf(10), lld.getRecursive(10));
        assertEquals(Integer.valueOf(220), lld.getRecursive(220));
        assertEquals(Integer.valueOf(5000), lld.getRecursive(5000));
    }

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> lld = new ArrayDeque<>();
        for (int i = 0; i < 10000; i++) {
            lld.addLast(i);
        }
        Iterator<Integer> iterator = lld.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            int i = iterator.next();
            assertEquals(n, i);
            n += 1;
        }
    }

    @Test
    public void equalTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        for (int i = 0; i < 10000; i++) {
            lld1.addLast(i);
        }
        ArrayDeque<Integer> lld2 = new ArrayDeque<>();
        for (int i = 0; i < 10000; i++) {
            lld2.addLast(i);
        }
        ArrayDeque<Integer> lld3 = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            lld3.addLast(i);
        }
        assertTrue(lld1.equals(lld2));
        assertFalse(lld1.equals(lld3));

        LinkedListDeque<Integer> lld4 = new LinkedListDeque<>();
        for (int i = 0; i < 10000; i++) {
            lld4.addLast(i);
        }
        assertTrue(lld1.equals(lld4));
    }

    /**
     * Test LinkedListDeque and ArrayDeque randomly.
     */
    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> buggyAList = new LinkedListDeque<>();
        int N = 500000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyAList.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                assertEquals(size, buggyAList.size());
            } else if (operationNumber == 2) {
                if (L.size() == 0) {
                    continue;
                }
                int index = StdRandom.uniform(0, L.size());
                Integer lastItem = L.get(index);
                assertEquals(lastItem, buggyAList.get(index));
            } else if (operationNumber == 3) {
                if (L.size() == 0) {
                    continue;
                }
                Integer lastItem = L.removeLast();
                assertEquals(lastItem, buggyAList.removeLast());
            }
        }
    }

}

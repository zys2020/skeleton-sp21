package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        int[] values = new int[]{5, 20, 37, 9, 10, 5, 66};
        AListNoResizing<Integer> aList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        for (int i = 0; i < values.length; i++) {
            aList.addLast(values[i]);
            buggyAList.addLast(values[i]);
        }
        int value;
        int[] aListValues = new int[values.length];
        int[] buggyAListValues = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            value = aList.removeLast();
            aListValues[i] = value;
            value = buggyAList.removeLast();
            buggyAListValues[i] = value;
        }
        assertArrayEquals(aListValues, buggyAListValues);
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        int N = 5000;
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
                Integer lastItem = L.getLast();
                assertEquals(lastItem, buggyAList.getLast());
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

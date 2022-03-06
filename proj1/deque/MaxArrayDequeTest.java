package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    public void multipleComparatorsTest() {
        Comparator<Integer> comparator1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
        MaxArrayDeque<Integer> maxArrayDeque1 = new MaxArrayDeque<>(comparator1);
        for (int i = 0; i < 10; i++) {
            maxArrayDeque1.addFirst(i);
        }
        assertEquals(Integer.valueOf(9), maxArrayDeque1.max());

        Comparator<String> comparator2 = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        };

        MaxArrayDeque<String> maxArrayDeque2 = new MaxArrayDeque<>(comparator2);
        maxArrayDeque2.addFirst("qwe");
        maxArrayDeque2.addFirst("qwesfgg");
        maxArrayDeque2.addFirst("qwesdf");
        maxArrayDeque2.addFirst("qwewewefe");
        assertEquals("qwewewefe", maxArrayDeque2.max());
    }

}

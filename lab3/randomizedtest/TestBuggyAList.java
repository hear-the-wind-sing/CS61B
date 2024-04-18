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
        AListNoResizing<Integer> noBugAlist = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        for (int i = 0; i < 3; i++) {
            noBugAlist.addLast(4 + i);
            buggyAList.addLast(4 + i);
        }

        for (int i = 0; i < 3; i++) {
            assertEquals(noBugAlist.removeLast(), buggyAList.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyAList.addLast(randVal);
                assertEquals(L.size(), buggyAList.size());
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                if (L.size() > 0) {
                    int remove = L.removeLast();
                    int removeBuglist = buggyAList.removeLast();
                    assertEquals(remove, removeBuglist);
                    System.out.println("removeLast: " + remove);
                }
            } else {
                if (L.size() > 0) {
                    assertEquals(L.getLast(), buggyAList.getLast());
                    System.out.println("getLast: " + L.getLast());
                }
            }
        }
    }
}

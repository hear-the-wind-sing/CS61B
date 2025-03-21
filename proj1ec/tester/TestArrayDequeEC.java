package tester;
import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;
public class TestArrayDequeEC {
    @Test
    public void test1(){
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();
        String s="";
        for( ; ; ) {
            int a = StdRandom.uniform(4);
            int c = StdRandom.uniform(30);
            Integer b = c;
            if(a == 0) {
                sad.addFirst(b);
                ads.addFirst(b);
                s += "addFirst("+b.toString()+")\n";
                continue;
            }
            if(a == 1) {
                sad.addLast(b);
                ads.addLast(b);
                s += "addLast("+b.toString()+")\n";
                continue;
            }
            int size1 = sad.size();
            int size2 = ads.size();
            if(size1 != size2) {
                s += "size()\n";
                assertEquals(s, size2, size1);
            }
            if(size2 == 0) continue;
            if(a == 2) {
                s += "removeFirst()\n";
                Integer actual = sad.removeFirst();
                Integer expected = ads.removeFirst();
                assertEquals(s, expected, actual);
            }
            if(a == 3) {
                s += "removeLast()\n";
                Integer actual = sad.removeLast();
                Integer expected = ads.removeLast();
                assertEquals(s, expected, actual);
            }
        }

    }
}

package gitlet;
import static org.junit.Assert.*;
import org.junit.Test;
public class test {
    @Test
    public void test1() {
        Commit a = new Commit("a",null);
        System.out.println(a.getCommitSha1());

        System.out.println(a.getCommitSha1());
    }
}

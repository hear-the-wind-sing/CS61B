package gitlet;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.io.File;
import static gitlet.Utils.*;
public class test {
    public static final File CWD = new File(System.getProperty("user.dir"));
    @Test
    public void test1() {
        Commit a = new Commit("a",null);
        File apath = join(CWD, "a");
        System.out.println(a.getCommitSha1());
        writeObject(apath,a);
        Commit b = readObject(apath,Commit.class);
        System.out.println(b.getCommitSha1());
    }
}

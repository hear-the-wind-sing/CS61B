package gitlet;

//
import java.io.File;
import java.io.Serializable;
import java.util.Date; //
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author hear-the-wind-sing
 */
public class Commit implements Serializable, Dumpable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date date;
    private String firstParent;
    private String secondParent;
    private HashMap<String, String> blobSha1;
    //private
    /*  */
    public Commit(String message, String firstParent) {
        this.message = message;
        if (firstParent == null) {
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
        this.firstParent = firstParent;
        this.secondParent = null;
        this.blobSha1 = new HashMap<>();
    }

    public Commit(String message, String firstParent, Commit lastCommit,
                  HashMap<String, String> index) {
        this.message = message;
        this.date = new Date();
        this.firstParent = firstParent;
        this.secondParent = null;
        this.blobSha1 = new HashMap<>(lastCommit.getBlobSha1());
        //this.blobSha1 = lastCommit.getBlobSha1();
        //this.blobSha1.putAll(index);
        for (Map.Entry<String, String> entry:index.entrySet()) {
            String filename = entry.getKey();
            String sha1 = entry.getValue();
            if (sha1 == null && this.blobSha1.containsKey(filename)) {
                this.blobSha1.remove(filename);
            } else {
                this.blobSha1.put(filename, sha1);
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getFirstParent() {
        return firstParent;
    }

    public String getSecondParent() {
        return secondParent;

    }
    public HashMap<String, String> getBlobSha1() {
        return blobSha1;
    }
    public String find(String fileName) {
        return blobSha1.get(fileName);
    }

    @Override
    public void dump() {
        System.out.println(getMessage());
        System.out.println(getDate());
    }
    public void print(String sha1) {
        System.out.println("===");
        System.out.println("commit " + sha1);
        if (firstParent != null && secondParent != null) {
            System.out.println("Merge: " + firstParent.substring(0, 7) + " "
                    + secondParent.substring(0, 7));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        System.out.println("Date: " + dateFormat.format(date));
        // System.out.println("Date: " + date.toString());
        System.out.println(message);
        System.out.println();
    }

    //    public String getCommitSha1() {
    //        return sha1(serialize(this));
    //    }
    public String getCommitSha1() {
        StringBuilder tmp = new StringBuilder();
        for (String name : blobSha1.keySet()) {
            tmp.append(blobSha1.get(name));
        }
        String fp = firstParent, sp = secondParent;
        if (firstParent == null) {
            fp = "";
        }
        if (secondParent == null) {
            sp = "";
        }
        return Utils.sha1(message, date.toString(), fp, sp, tmp.toString());
    }
    public void setSecondParent(String sha1) {
        this.secondParent = sha1;
    }
    public String getBlobContent(String fileName) {
        String sha1 = this.blobSha1.get(fileName);
        File blobPath = Repository.getObjectFile(sha1);
        Blob b = readObject(blobPath, Blob.class);
        return new String(b.getContent());
    }
}

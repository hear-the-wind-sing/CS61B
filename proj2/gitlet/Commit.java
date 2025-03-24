package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;
import java.util.HashMap;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
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
    /* TODO: fill in the rest of this class. */
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

    public Commit(String message, String firstParent, Commit lastCommit, HashMap<String, String> index) {
        this.message = message;
        this.date = new Date();
        this.firstParent = firstParent;
        this.secondParent = null;
        this.blobSha1 = lastCommit.getBlobSha1();
        this.blobSha1.putAll(index);
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
        System.out.println("---");
        System.out.println("commit " + sha1);
        if (firstParent != null && secondParent != null) {
            System.out.println("Merge: " + firstParent.substring(0, 7) + " " + secondParent.substring(0, 7));
        }
        System.out.println("Date: " + date.toString());
        System.out.println(message);
        System.out.println();
    }
}

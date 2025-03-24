package gitlet;


import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable, Dumpable {
    //private static final long serialVersionUID = 1L;
    //private final String filename;  // 可选：关sa联文件名（非必须）
    private final byte[] content;   // 文件内容
    //private final String sha1;      // 内容的 SHA1 哈希

    public Blob(File file) {
        //this.filename = file.getName();
        this.content = Utils.readContents(file);
        //this.sha1 = Utils.sha1(content);
    }

    public Blob(byte[] a) {
        content = a;
    }


    // Getter 方法
    public byte[] getContent() {
        return content;
    }
    // public String getSha1() { return sha1; }

    public String getContentAsString() {
        return new String(content);
    }

    @Override
    public void dump() {
        System.out.println(getContentAsString());
    }
}

package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here
import java.util.Map;
import java.util.HashMap;
/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    /**
     * object文件夹存放 blob 和 commit
     */
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");

    /**
     * refs文件夹
     */
    public static final File REFS = join(GITLET_DIR, "refs");

    /**
     * heads存放不同分支
     */
    public static final File HEADS = join(GITLET_DIR, "refs/heads");
    /**
     * index文件存“暂存区”
     */
    public static final File INDEX = join(GITLET_DIR, "index");
    /* TODO: fill in the rest of this class. */
    /**
     * HEAD文件存放所在commit
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static HashMap<String, String> index = new HashMap<>();

    public static void init() {

        GITLET_DIR.mkdir();
        OBJ_DIR.mkdir();
        REFS.mkdir();
        HEADS.mkdir();

        try {
            INDEX.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Commit initCommit = new Commit("initial commit", null);
        String commitSha1 = sha1(serialize(initCommit));
        File commitFile = getObjectFile(commitSha1);
        writeObject(commitFile, initCommit);

        File master = join(HEADS, "master");
        writeContents(master, commitSha1);
        writeContents(HEAD, "ref: refs/heads/master");

        writeObject(INDEX, index);
    }

    public static void add(String[] args) {

        File filePath = join(CWD, args[1]);
        String fileName = args[1];

        /** 判断文件是否存在*/
        if (!filePath.exists()) {
            message("File does not exist.");
            System.exit(0);
        }

        /** 读出当前commmit的sha1,取出当前commit*/
        //        String curCommitSha1 = readContentsAsString(HEAD);
        String curCommitSha1 = getCurrentCommitSha1();
        File curCommitPath = getObjectFile(curCommitSha1);
        Commit curCommit = readObject(curCommitPath, Commit.class);

        byte[] fileContent = readContents(filePath);
        String workFileSha1 = sha1(fileContent);
        String curFileSha1 = curCommit.find(fileName);

        if (curFileSha1 == null || workFileSha1 != curFileSha1) {
            Blob b = new Blob(fileContent);
            saveBlob(b, workFileSha1);
            index = readObject(INDEX, HashMap.class);
            index.put(fileName, workFileSha1);
            writeObject(INDEX, index);
        }
    }

    public static void commit(String[] args) {

        index = readObject(INDEX, HashMap.class);

        if (index.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        if (args.length == 1) {
            message("Please enter a commit message.");
            System.exit(0);
        }

        /** 读出当前commmit的sha1,取出当前commit*/
        //String curCommitSha1 = readContentsAsString(HEAD);
        String curCommitSha1 = getCurrentCommitSha1();
        File curCommitPath = getObjectFile(curCommitSha1);
        Commit curCommit = readObject(curCommitPath, Commit.class);

        /** 新建commit,清空暂存区*/
        Commit newCommit = new Commit(args[1], curCommitSha1, curCommit, index);
        index.clear();

        /** 将最新commit的sha1写入 branch*/
        String newCommitSha1 = sha1(serialize(newCommit));
        writeContents(HEAD, newCommitSha1);

        /** 将最新commit写入objects */
        saveCommit(newCommit, newCommitSha1);
    }

    public static void checkout(String[] args) {
        /** java gitlet.Main checkout -- [file name] */
        if (args[1].equals("--")) {
            String fileName = args[2];
            /** 取出当前commit, 以及它的map*/
            Commit curCommit = getCurrentCommit();
            HashMap<String, String> curBlobSha1 = curCommit.getBlobSha1();

            /** 查看有无要求文件*/
            if (!curBlobSha1.containsKey(fileName)) {
                message("File does not exist in that commit.");
                System.exit(0);
            }

            /** 写出要求文件*/
            String fileSha1 = curBlobSha1.get(fileName);
            Blob b = getBlob(fileSha1);
            File filePath = join(CWD, fileName);
            writeContents(filePath, b.getContent());
        } else if (args.length == 2) {
            /** java gitlet.Main checkout [branch name] */
            File checkBranch = join(HEADS, args[1]);

            /**是否存在目标分支*/
            if (!checkBranch.exists()) {
                message("No such branch exists.");
                System.exit(0);
            }

            String headContent = readContentsAsString(HEAD);
            String branchName = headContent.substring("ref: refs/heads/".length());

            /**是否是当前分支*/
            if (checkBranch.equals(branchName)) {
                message("No need to checkout the current branch.");
                System.exit(0);
            }

            /** 取出map*/
            Commit curCommit = getCurrentCommit();
            Commit branchCommit = getBranchCommit(args[1]);
            HashMap<String, String> curBlobSha1 = curCommit.getBlobSha1();
            HashMap<String, String> branchBlobSha1 = branchCommit.getBlobSha1();

            /** 如果工作文件在当前分支中未被跟踪，并且将被签出、打印
             *  There is an untracked file in the way; delete it,or add and commit it first.*/
            for (String filename : branchBlobSha1.keySet()) {
                if (!curBlobSha1.containsKey(filename)) {
                    File file = join(CWD, filename);
                    if (file.exists()) {
                        message("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }

            /** 删除在当前分支中跟踪但不存在于签出分支中的任何文件*/
            for (String filename : curBlobSha1.keySet()) {
                if (!branchBlobSha1.containsKey(filename)) {
                    File fileToDelete = join(CWD, filename);
                    if (fileToDelete.exists()) {
                        restrictedDelete(fileToDelete);
                    }
                }
            }

            /** 写入目标分支文件到工作目录*/
            for (Map.Entry<String, String> entry : branchBlobSha1.entrySet()) {
                String filename = entry.getKey();
                String blobSha1 = entry.getValue();
                File filePath = join(CWD, filename);
                // 从对象库读取 Blob 内容并写入文件
                Blob blob = getBlob(blobSha1);
                writeContents(filePath, blob.getContent());
            }

            /** 将当前分支写入HEAD */
            writeContents(HEAD, "ref: refs/heads/" + branchName);

            /** 清空暂存区*/
            HashMap<String, String> index = new HashMap<>();
            writeObject(INDEX, index);
        } else {
            /** java gitlet.Main checkout [commit id] -- [file name]*/
            String fileName = args[3];

            /** 有无要求的commit*/
            File commitFile = getObjectFile(args[1]);
            if (!commitFile.exists()) {
                message("No commit with that id exists.");
                System.exit(0);
            }

            /** 取出所需commit, 以及它的map*/
            Commit commit = getCommit(args[1]);
            HashMap<String, String> blobSha1 = commit.getBlobSha1();

            /** 查看有无要求文件*/
            if (!blobSha1.containsKey(fileName)) {
                message("File does not exist in that commit.");
                System.exit(0);
            }

            /** 写出要求文件*/
            String fileSha1 = blobSha1.get(fileName);
            Blob b = getBlob(fileSha1);
            File filePath = join(CWD, fileName);
            writeContents(filePath, b.getContent());
        }
    }

    public static void log() {
        String commitSha1 = getCurrentCommitSha1();
        Commit commit = getCommit(commitSha1);
        commit.print(commitSha1);
        while (commit.getFirstParent() != null) {
            String parentCommitSha1 = commit.getFirstParent();
            Commit parentCommit = getCommit(parentCommitSha1);
            parentCommit.print(parentCommitSha1);
            commit = parentCommit;
        }
    }


    //    public static void save_commit(Commit ){
    //
    //    }

    /**
     * 创建一个object文件所在的文件夹并返回要存储的位置
     * 例如 一个 blob 的sha1 是 001a...
     * 创建00,返回1a...
     */
    public static File getObjectFile(String sha1) {
        // 提取前2位作为子目录名，剩余字符作为文件名
        String dirName = sha1.substring(0, 2);
        String fileName = sha1.substring(2);

        // 构建路径：.gitlet/objects/dirName/fileName
        File subDir = join(OBJ_DIR, dirName);
        subDir.mkdirs(); // 自动创建目录（如果不存在）

        return join(subDir, fileName);
    }

    public static void saveBlob(Blob b, String sha1) {
        File savePath = getObjectFile(sha1);
        writeObject(savePath, b);
    }

    public static Blob getBlob(String sha1) {
        File blobPath = getObjectFile(sha1);
        return readObject(blobPath, Blob.class);
    }
    //    public static void dumpobj(String sha1){
    //        File path = getObjectFile(sha1);
    //        Blob b = readObject(path, Blob.class);
    //        System.out.println(b.getContent());
    //    }

    public static void saveCommit(Commit c, String sha1) {
        File savePath = getObjectFile(sha1);
        writeObject(savePath, c);
    }
    public static String getCurrentCommitSha1() {
        // 1. 读取 HEAD 文件内容
        String headContent = readContentsAsString(HEAD);

        String commitSha1;
        if (headContent.startsWith("ref: ")) {
            // 2. 解析分支路径（例如 ref: refs/heads/master → master）
            String branchName = headContent.substring("ref: refs/heads/".length());
            File branchFile = join(HEADS, branchName);

            // 3. 读取分支文件中的提交 SHA1
            if (!branchFile.exists()) {
                throw new GitletException("Branch does not exist: " + branchName);
            }
            commitSha1 = readContentsAsString(branchFile);
        } else {
            // 4. 分离头指针状态：HEAD 直接存储 SHA1
            commitSha1 = headContent;
        }
        return commitSha1;
    }
    public static Commit getCurrentCommit() {
        String curCommitSha1 = getCurrentCommitSha1();
        File curCommitPath = getObjectFile(curCommitSha1);
        return readObject(curCommitPath, Commit.class);
    }

    public static Commit getBranchCommit(String branchName) {
        File branchFile = join(HEADS, branchName);
        String commitSha1 = readContentsAsString(branchFile);
        File commitPath = getObjectFile(commitSha1);
        return readObject(commitPath, Commit.class);
    }

    public static Commit getCommit(String commitSha1) {
        File commitFile = getObjectFile(commitSha1);
        return readObject(commitFile, Commit.class);
    }
}

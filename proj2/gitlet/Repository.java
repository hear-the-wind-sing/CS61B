package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

//
import java.util.*;

/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author hear-the-wind-sing
 */
public class Repository {
    /**
     *
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
    /*  */
    /**
     * HEAD文件存放所在commit
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    private static HashMap<String, String> index = new HashMap<>();

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
        //String commitSha1 = sha1(serialize(initCommit));
        String commitSha1 = initCommit.getCommitSha1();
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

        // 欲暂存文件的sha1
        byte[] fileContent = readContents(filePath);
        String workFileSha1 = sha1(fileContent);

        //当前commit的跟踪
        HashMap<String, String> curCommitBlobSha1 = curCommit.getBlobSha1();
        String curFileSha1 = curCommitBlobSha1.get(fileName);

        if (!curCommitBlobSha1.containsKey(fileName) || !workFileSha1.equals(curFileSha1)) {
            Blob b = new Blob(fileContent);
            saveBlob(b, workFileSha1);
            index = readObject(INDEX, HashMap.class);
            index.put(fileName, workFileSha1);
            writeObject(INDEX, index);
        }
        if (curCommitBlobSha1.containsKey(fileName)) {
            index = readObject(INDEX, HashMap.class);
            if (curFileSha1.equals(workFileSha1)) {
                if (index.containsKey(fileName)) {
                    index.remove(fileName);
                    writeObject(INDEX, index);
                }
                return;
            }
        }
    }

    public static void commit(String[] args) {
        //强行复用在merge里了，merge args.length == 3

        index = readObject(INDEX, HashMap.class);


        if (args.length != 3 && index.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        if (args.length == 1 || args[1].equals("")) {
            message("Please enter a commit message.");
            System.exit(0);
        }

        /** 读出当前commmit的sha1,取出当前commit*/
        //String curCommitSha1 = readContentsAsString(HEAD);
        String curCommitSha1 = getCurrentCommitSha1();
        File curCommitPath = getObjectFile(curCommitSha1);
        Commit curCommit = readObject(curCommitPath, Commit.class);

        /** 新建commit,清空暂存区,(记得写回到INDEX)*/
        Commit newCommit = new Commit(args[1], curCommitSha1, curCommit, index);
        index.clear();
        writeObject(INDEX, index);

        if (args.length == 3) {
            newCommit.setSecondParent(args[2]);
        }

        /** 将最新commit的sha1写入 branch*/
        //String newCommitSha1 = sha1(serialize(newCommit));
        String newCommitSha1 = newCommit.getCommitSha1();
        File nowBranch = getNowBranch();
        writeContents(nowBranch, newCommitSha1);


        /** 将最新commit写入objects */
        saveCommit(newCommit, newCommitSha1);
    }

    public static void checkout(String[] args) { /** java gitlet.Main checkout -- [file name] */
        if (args[1].equals("--")) {
            String fileName = args[2];
            Commit curCommit = getCurrentCommit(); /** 取出当前commit, 以及它的map*/
            HashMap<String, String> curBlobSha1 = curCommit.getBlobSha1();
            if (!curBlobSha1.containsKey(fileName)) {      /** 查看有无要求文件*/
                message("File does not exist in that commit.");
                System.exit(0);
            }
            String fileSha1 = curBlobSha1.get(fileName); /** 写出要求文件*/
            Blob b = getBlob(fileSha1);
            File filePath = join(CWD, fileName);
            writeContents(filePath, b.getContent());
        } else if (args.length == 2) {
            File checkBranch = join(HEADS, args[1]); /** java gitlet.Main checkout [branch name] */
            if (!checkBranch.exists()) {    /**是否存在目标分支*/
                message("No such branch exists.");
                System.exit(0);
            }
            String headContent = readContentsAsString(HEAD);
            String branchName = headContent.substring("ref: refs/heads/".length());
            if (args[1].equals(branchName)) {      /**是否是当前分支*/
                message("No need to checkout the current branch.");
                System.exit(0);
            }
            Commit curCommit = getCurrentCommit();     /** 取出map*/
            Commit branchCommit = getBranchCommit(args[1]);
            HashMap<String, String> curBlobSha1 = curCommit.getBlobSha1();
            HashMap<String, String> branchBlobSha1 = branchCommit.getBlobSha1();
            /** 如果工作文件在当前分支中未被跟踪，并且将被签出、打印
             *  There is an untracked file in the way; delete it,or add and commit it first.*/
            for (String filename : branchBlobSha1.keySet()) {
                if (!curBlobSha1.containsKey(filename)) {
                    File file = join(CWD, filename);
                    if (file.exists()) {
                        message("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }   /** 删除在当前分支中跟踪但不存在于签出分支中的任何文件*/
            for (String filename : curBlobSha1.keySet()) {
                if (!branchBlobSha1.containsKey(filename)) {
                    File fileToDelete = join(CWD, filename);
                    if (fileToDelete.exists()) {
                        restrictedDelete(fileToDelete);
                    }
                }
            }
            for (Map.Entry<String, String> entry : branchBlobSha1.entrySet()) {
                String filename = entry.getKey();  /** 写入目标分支文件到工作目录*/
                String blobSha1 = entry.getValue();
                File filePath = join(CWD, filename);
                // 从对象库读取 Blob 内容并写入文件
                Blob blob = getBlob(blobSha1);
                writeContents(filePath, blob.getContent());
            }
            writeContents(HEAD, "ref: refs/heads/" + args[1]);   /** 将目标分支写入HEAD */
            HashMap<String, String> newIndex = new HashMap<>();   /** 清空暂存区*/
            writeObject(INDEX, newIndex);
        } else {
            String fileName = args[3];  /** java gitlet.Main checkout [commit id] -- [file name]*/
            String commitSha1 = resolve(args[1]);     /** 有无要求的commit*/
            if (commitSha1 == null) {
                message("No commit with that id exists.");
                System.exit(0);
            }
            File commitFile = getObjectFile(args[1]);
            Commit commit = getCommit(commitSha1);    /** 取出所需commit, 以及它的map*/
            HashMap<String, String> blobSha1 = commit.getBlobSha1();
            if (!blobSha1.containsKey(fileName)) {     /** 查看有无要求文件*/
                message("File does not exist in that commit.");
                System.exit(0);
            }
            String fileSha1 = blobSha1.get(fileName);  /** 写出要求文件*/
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

    public static void rm(String[] args) {
        File rmfile = join(CWD, args[1]);

        Commit curCommit = getCurrentCommit();
        HashMap<String, String> curCommitbBlobSha1 = curCommit.getBlobSha1();
        index = readObject(INDEX, HashMap.class);

        if (!curCommitbBlobSha1.containsKey(args[1]) && !index.containsKey(args[1])) {
            message("No reason to remove the file.");
            System.exit(0);
        }

        if (index.containsKey(args[1])) {
            index.remove(args[1]);
        }
        if (curCommitbBlobSha1.containsKey(args[1])) {
            index.put(args[1], null);
            restrictedDelete(rmfile);
        }

        writeObject(INDEX, index);
    }

    //    public static void save_commit(Commit ){
    //
    //    }

    public static void globalLog() {
        File[] prefixDirs = OBJ_DIR.listFiles();
        for (File prefixDir : prefixDirs) {
            if (prefixDir.isDirectory()) {
                // 遍历每个子目录中的文件
                File[] objFiles = prefixDir.listFiles();
                for (File objFile : objFiles) {
                    try {
                        // 构建完整对象ID
                        String id = prefixDir.getName() + objFile.getName();

                        // 尝试反序列化为Commit对象
                        Commit c = readObject(objFile, Commit.class);

                        // 如果反序列化成功，打印
                        c.print(id);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }
            }
        }
    }

    public static void find(String[] args) {
        boolean flag = false;
        File[] prefixDirs = OBJ_DIR.listFiles();
        for (File prefixDir : prefixDirs) {
            if (prefixDir.isDirectory()) {
                // 遍历每个子目录中的文件
                File[] objFiles = prefixDir.listFiles();
                for (File objFile : objFiles) {
                    try {
                        // 构建完整对象ID
                        String id = prefixDir.getName() + objFile.getName();

                        // 尝试反序列化为Commit对象
                        Commit c = readObject(objFile, Commit.class);

                        if (c.getMessage().equals(args[1])) {
                            flag = true;
                            System.out.println(id);
                        }
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }
            }
        }
        if (!flag) {
            message("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void status() {
        List<String> words = new ArrayList<>();

        String headContent = readContentsAsString(HEAD);
        String branchName = headContent.substring("ref: refs/heads/".length());
        System.out.println("=== Branches ===");
        File[] branches = HEADS.listFiles();
        for (File branch: branches) {
            words.add(branch.getName());
        }
        Collections.sort(words);
        for (String e: words) {
            if (e.equals(branchName)) {
                System.out.println("*" + e);
            } else {
                System.out.println(e);
            }
        }
        System.out.println();
        words.clear();

        index = readObject(INDEX, HashMap.class);
        System.out.println("=== Staged Files ===");
        for (Map.Entry<String, String> entry: index.entrySet()) {
            String filename = entry.getKey();
            String blobSha1 = entry.getValue();
            if (blobSha1 != null) {
                //System.out.println(filename);
                words.add(filename);
            }
        }
        Collections.sort(words);
        for (String e: words) {
            System.out.println(e);
        }
        System.out.println();
        words.clear();

        System.out.println("=== Removed Files ===");
        for (Map.Entry<String, String> entry: index.entrySet()) {
            String filename = entry.getKey();
            String blobSha1 = entry.getValue();
            if (blobSha1 == null) {
                words.add(filename);
            }
        }
        Collections.sort(words);
        for (String e: words) {
            System.out.println(e);
        }
        System.out.println();
        words.clear();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void branch(String[] args) {
        File branch = join(HEADS, args[1]);

        if (branch.exists()) {
            message("A branch with that name already exists.");
            System.exit(0);
        }

        String curCommitSha1 = getCurrentCommitSha1();
        writeContents(branch, curCommitSha1);
    }

    public static void rmBranch(String[] args) {
        File branch = join(HEADS, args[1]);

        if (!branch.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }

        String headContent = readContentsAsString(HEAD);
        String curBranchName = headContent.substring("ref: refs/heads/".length());

        if (args[1].equals(curBranchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }

        branch.delete();
    }

    public static void reset(String[] args) {
        //reset [commit id]  先找到完整sha1  写烂了，只能这样减少改动了
        String resetCommitSha1 = resolve(args[1]);

        if (resetCommitSha1 == null) {
            message("No commit with that id exists.");
            System.exit(0);
        }

        //尝试复用checkout代码
        /** 取出map*/
        Commit curCommit = getCurrentCommit();
        Commit resetCommit = getCommit(args[1]);
        HashMap<String, String> curBlobSha1 = curCommit.getBlobSha1();
        HashMap<String, String> resetBlobSha1 = resetCommit.getBlobSha1();

        /** 如果工作文件在当前分支中未被跟踪，并且将被签出、打印
         *  There is an untracked file in the way; delete it,or add and commit it first.*/
        for (String filename : resetBlobSha1.keySet()) {
            if (!curBlobSha1.containsKey(filename)) {
                File file = join(CWD, filename);
                if (file.exists()) {
                    message("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }

        /** 删除在当前分支中跟踪但不存在于签出分支中的任何文件*/
        for (String filename : curBlobSha1.keySet()) {
            if (!resetBlobSha1.containsKey(filename)) {
                File fileToDelete = join(CWD, filename);
                if (fileToDelete.exists()) {
                    restrictedDelete(fileToDelete);
                }
            }
        }

        /** 写入目标分支文件到工作目录*/
        for (Map.Entry<String, String> entry : resetBlobSha1.entrySet()) {
            String filename = entry.getKey();
            String blobSha1 = entry.getValue();
            File filePath = join(CWD, filename);
            // 从对象库读取 Blob 内容并写入文件
            Blob blob = getBlob(blobSha1);
            writeContents(filePath, blob.getContent());
        }

        /** 将当前分支指向要求的commit*/
        File nowBranch = getNowBranch();
        writeContents(nowBranch, args[1]);

        /** 清空暂存区*/
        HashMap<String, String> newIndex = new HashMap<>();
        writeObject(INDEX, newIndex);
    }

    public static void merge(String[] args) {
        index = readObject(INDEX, HashMap.class);
        if (!index.isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        File branch = join(HEADS, args[1]);
        if (!branch.exists())  {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        File nowBranch = getNowBranch();
        if (branch.getName().equals(nowBranch.getName())) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        String splitCommitSha1 = "";  //取出commit
        String branchCommitSha1 = readContentsAsString(branch);
        String nowBranchCommitSha1 = readContentsAsString(nowBranch);
        Commit splitCommit = null;
        Commit branchCommit = getCommit(branchCommitSha1);
        Commit nowBranchCommit = getCommit(nowBranchCommitSha1);
        Queue<String> queueA = new LinkedList<>(); //得到split point
        Queue<String> queueB = new LinkedList<>();
        HashMap<String, Integer> visitedA = new HashMap<>(); // SHA-1 → depth
        HashMap<String, Integer> visitedB = new HashMap<>();
        queueA.add(branchCommitSha1);
        visitedA.put(branchCommitSha1, 0);
        queueB.add(nowBranchCommitSha1);
        visitedB.put(nowBranchCommitSha1, 0);

        splitCommitSha1 = getSplitSha1(queueA, queueB, visitedA, visitedB);
        splitCommit = getCommit(splitCommitSha1);

        if (splitCommitSha1.equals(branchCommitSha1)) {  //先处理在一条路上的情况
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitCommitSha1.equals(nowBranchCommitSha1)) {
            String[] a = {"checkout", args[1]};
            checkout(a);
            message("Current branch fast-forwarded.");
            System.exit(0);
        }          //三方文件差异比较,签出 暂存
        HashMap<String, String> newIndexForCommit = getNewIndexForCmt(nowBranchCommit,
                branchCommit, splitCommit);
        HashMap<String, String> curBlobSha1 = nowBranchCommit.getBlobSha1();
        for (String filename : newIndexForCommit.keySet()) {
            if (!curBlobSha1.containsKey(filename)) {
                File file = join(CWD, filename);
                if (file.exists()) {
                    byte[] fileContent = readContents(file);
                    String fileSha1 = sha1(fileContent);
                    if (!fileSha1.equals(newIndexForCommit.get(filename))) {
                        message("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
        }
        for (HashMap.Entry<String, String> entry : newIndexForCommit.entrySet()) {
            String filename = entry.getKey();
            String blobSha1 = entry.getValue();
            File filePath = join(CWD, filename);
            if (blobSha1 == null) {
                restrictedDelete(filePath);
            } else {
                Blob blob = getBlob(blobSha1);
                writeContents(filePath, blob.getContent());
            }
        }
        writeObject(INDEX, newIndexForCommit); //写入暂存区
        String[] a = {"commit", "Merged " + branch.getName() + " into "
                + nowBranch.getName() + ".", branchCommitSha1};
        Repository.commit(a);           //自动提交
    }

    public static String getSplitSha1(Queue<String> queueA, Queue<String> queueB,
                                      HashMap<String, Integer> visitedA,
                                      HashMap<String, Integer> visitedB) {
        String splitCommitSha1 = "";
        boolean findLca = false;
        while (!findLca && (!queueA.isEmpty() || !queueB.isEmpty())) {
            int sizeA = queueA.size(); // 检查队列A的当前层
            for (int i = 0; i < sizeA; i++) {
                String currentSha1 = queueA.poll();
                if (visitedB.containsKey(currentSha1)) {
                    splitCommitSha1 = currentSha1;
                    findLca = true;
                    break;
                }
                addParentsToQueue(currentSha1, queueA, visitedA);
            }
            if (findLca) {
                break;
            }
            int sizeB = queueB.size(); // 检查队列B的当前层
            for (int i = 0; i < sizeB; i++) {
                String currentSha1 = queueB.poll();
                if (visitedA.containsKey(currentSha1)) {
                    splitCommitSha1 = currentSha1;
                    findLca = true;
                    break;
                }
                addParentsToQueue(currentSha1, queueB, visitedB);
            }
        }
        return splitCommitSha1;
    }
    public static HashMap<String, String> getNewIndexForCmt(Commit currentHead,
                                                            Commit otherHead, Commit splitCommit) {
        boolean conflictDetected = false;
        HashMap<String, String> newIndex = new HashMap<>();

        // 获取各个提交中的文件与其对应的SHA-1值（blobs）
        HashMap<String, String> splitBlobs = splitCommit.getBlobSha1();
        HashMap<String, String> currentBlobs = currentHead.getBlobSha1();
        HashMap<String, String> otherBlobs = otherHead.getBlobSha1();

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitBlobs.keySet());
        allFiles.addAll(currentBlobs.keySet());
        allFiles.addAll(otherBlobs.keySet());

        for (String fileName : allFiles) {
            String splitSha1 = splitBlobs.get(fileName);
            String headSha1 = currentBlobs.get(fileName);
            String otherSha1 = otherBlobs.get(fileName);
            if (splitSha1 != null && headSha1 != null && otherSha1 != null) {
                if (splitSha1.equals(headSha1) && !splitSha1.equals(otherSha1)) {
                    newIndex.put(fileName, otherSha1);
                }
                if (!splitSha1.equals(headSha1)
                        && !splitSha1.equals(otherSha1) && !headSha1.equals(otherSha1)) {
                    conflictDetected = true;
                    handleConflict(fileName, headSha1, otherSha1, newIndex);
                }
            }
            if (splitSha1 != null) {
                if (splitSha1.equals(headSha1) && otherSha1 == null) {
                    newIndex.put(fileName, null);
                }
                if (headSha1 != null && otherSha1 == null && !splitSha1.equals(headSha1)) {
                    conflictDetected = true;
                    handleConflict(fileName, headSha1, otherSha1, newIndex);
                }
                if (otherSha1 != null && headSha1 == null && !splitSha1.equals(otherSha1)) {
                    conflictDetected = true;
                    handleConflict(fileName, headSha1, otherSha1, newIndex);
                }
            }
            if (splitSha1 == null) {
                if (otherSha1 != null) {
                    if (headSha1 != null && !headSha1.equals(otherSha1)) {
                        //冲突
                        conflictDetected = true;
                        handleConflict(fileName, headSha1, otherSha1, newIndex);
                    }
                    newIndex.put(fileName, otherSha1);
                }
            }
        }

        // 如果发生了冲突，打印冲突信息
        if (conflictDetected) {
            System.out.println("Encountered a merge conflict.");
        }

        return newIndex;
    }

    private static void handleConflict(String fileName, String currentSha1, String otherSha1,
                                       HashMap<String, String> newIndex) {
        String currentContent = "";
        if (currentSha1 != null) {
            Blob currentBlob = getBlob(currentSha1);
            currentContent = new String(currentBlob.getContent());
        }

        String otherContent = "";
        if (otherSha1 != null) {
            Blob otherBlob = getBlob(otherSha1);
            otherContent = new String(otherBlob.getContent());
        }

        // 生成冲突内容
        String conflictContent =  "<<<<<<< HEAD\n" + currentContent
                + "=======\n" + otherContent + ">>>>>>>\n";

        //调试
        //System.out.println(conflictContent);

        // 创建冲突blob
        Blob conflictBlob = new Blob(conflictContent.getBytes());
        String conflictSha1 = sha1(conflictContent.getBytes());
        saveBlob(conflictBlob, conflictSha1); // 确保有保存blob的方法

        newIndex.put(fileName, conflictSha1);
    }
    private static void addParentsToQueue(String commitSha1,
                                          Queue<String> queue,
                                          HashMap<String, Integer> visited)  {
        Commit commit = getCommit(commitSha1);
        Integer currentDepth = visited.get(commitSha1);
        //System.out.println(currentDepth.toString());

        String firstParentSha1 = commit.getFirstParent();
        String secondParentSha1 = commit.getSecondParent();
        if (firstParentSha1 != null && !visited.containsKey(firstParentSha1)) {
            Commit firstParent = getCommit(firstParentSha1);
            visited.put(firstParentSha1, currentDepth + 1);
            queue.add(firstParentSha1);
        }
        if (secondParentSha1 != null && !visited.containsKey(secondParentSha1)) {
            Commit secondParent = getCommit(secondParentSha1);
            visited.put(secondParentSha1, currentDepth + 1);
            queue.add(secondParentSha1);
        }
    }
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

    public static File getNowBranch() {
        String headContent = readContentsAsString(HEAD);
        String branchName = headContent.substring("ref: refs/heads/".length());
        return join(HEADS, branchName);
    }

    public static String resolve(String shortHash) {
        // 校验基本格式
        if (shortHash.length() < 2 || !shortHash.matches("[0-9a-fA-F]+")) {
            message("Invalid commit id.");
            System.exit(0);
        }

        // 统一转为小写
        String lowerHash = shortHash.toLowerCase();
        String dirName = lowerHash.substring(0, 2);
        String filePrefix = lowerHash.length() > 2 ? lowerHash.substring(2) : "";

        // 定位objects子目录
        File objDir = join(OBJ_DIR, dirName);
        if (!objDir.exists()) {
            return null;
        }

        String fullHash = null;
        for (File f : objDir.listFiles()) {
            String name = dirName + f.getName();
            if (name.startsWith(lowerHash)) {
                if (fullHash != null) { // 发现多个匹配
                    message("Ambiguous commit id.");
                    System.exit(0);
                }
                fullHash = name;
            }
        }

        return fullHash;
    }
}

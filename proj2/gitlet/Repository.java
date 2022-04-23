package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author Yuansong Zhang
 */
public class Repository {
    /**
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
     * The .gitlet/commit directory save commit instance of Commit class.
     */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");

    /**
     * The .gitlet/stagingArea directory caching files added.
     */
    public static final File STAGING_DIR = join(GITLET_DIR, "stagingArea");

    /**
     * The .gitlet/objects directory saving files committed.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /**
     * The .gitlet/refs directory commit references.
     */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");

    /**
     * The .gitlet/refs/heads directory saving branches.
     */
    public static final File HEAEDS_DIR = join(REFS_DIR, "heads");

    /**
     * The .gitlet/refs/remotes directory saving branches.
     */
    public static final File REMOTE_DIR = join(REFS_DIR, "remotes");

    /**
     * The .gitlet/refs/tags directory saving branches.
     */
    public static final File TAGS_DIR = join(REFS_DIR, "tags");

    public static String author = "auto";

    public static String currentBranch;

    public static Commit currentHead;

    /**
     * Staging blobs whose Key is a filename and Value is the SHA1 code of the file.
     */
    public static HashMap<String, String> blobMap = new HashMap<>();

    /**
     * Committed blobs whose Key is a filename and Value is the SHA1 code of the file.
     */
    public static HashMap<String, String> committedBlobMap = new HashMap<>();

    /**
     * The timestamp of each commit is not a local time but a standard time
     */
    public static final String TIME_ZONE = "GMT";

    public static void clear() {
        deleteDirectory(Repository.GITLET_DIR);
    }

    /**
     * `git init` command
     */
    public static void init() {
        if (Repository.GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Repository.currentBranch = "main";
        String message = "initial commit";
        String timeString = "1970-01-01 00:00:00 +0000";
        String hash = sha1(message, Repository.author, timeString);
        Commit commit = new Commit(message, Repository.author, timeString, hash, null, null, null);
        Repository.currentHead = commit;
        // write the head point of the branch
        Repository.writeHead();
        File file = join(COMMIT_DIR, commit.hash);
        // write the commit instance
        writeObject(file, commit);
    }

    private static void writeHead() {
        Repository.writeHead(Repository.currentBranch, Repository.currentHead.hash);
    }

    private static void writeHead(String branch, String commitHash) {
        writeContents(join(Repository.HEAEDS_DIR, branch), commitHash);
    }

    /**
     * `git add` command
     */
    public static void add(String filename) {
        if (!join(Repository.CWD, filename).exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] content = readContents(join(Repository.CWD, filename));
        String hash = sha1(content);
        if (Repository.committedBlobMap.containsKey(filename) &&
                Repository.committedBlobMap.get(filename).equals(hash)) {
            return;
        }
        if (Repository.blobMap.containsKey(filename) &&
                Repository.blobMap.get(filename).equals(hash)) {
            return;
        }
        File file = hashFilename(STAGING_DIR, hash, "add");
        writeContents(file, content);
        Repository.blobMap.put(filename, hash);
    }

    public static void add(String[] filenameArray) {
        for (String filename : filenameArray) {
            Repository.add(filename);
        }
    }

    public static File hashFilename(File parentDir, String hash, String mode) {
        String prefix;
        if (mode == null) {
            prefix = "";
        } else if (mode.equals("add")) {
            prefix = mode;
        } else if (mode.equals("remove")) {
            prefix = mode;
        } else {
            throw new IllegalArgumentException("Illegal mode `" + mode + "`, only `add` or `remove` is legal.");
        }
        File path = join(parentDir, prefix + "/" + hash.substring(0, 2));
        return join(path, hash.substring(2));
    }

    /**
     * `git commit` command
     */
    public static void commit(String message) {
        if (Repository.blobMap.isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        }
        if (message == null || message.equals("")) {
            System.out.println("Please enter a commit message");
            return;
        }
        String timeString = convertDateToString(new Date(), Repository.TIME_ZONE);
        String hash = sha1(message, Repository.author, timeString);
        String parentHash = Repository.currentHead.hash;

        for (String key : Repository.blobMap.keySet()) {
            File file = hashFilename(STAGING_DIR, Repository.blobMap.get(key), "add");
            if (file.exists()) {
                Repository.committedBlobMap.put(key, Repository.blobMap.get(key));
            } else {
                Repository.committedBlobMap.remove(key);
            }
        }

        Commit commit = new Commit(message, Repository.author, timeString, hash, parentHash,
                null, Repository.committedBlobMap);
        Repository.currentHead = commit;
        Repository.writeHead();
        File file = join(COMMIT_DIR, commit.hash);
        writeObject(file, commit);
        Repository.blobMap.clear();

        moveDirectory(join(Repository.STAGING_DIR, "add"), Repository.OBJECTS_DIR);
        deleteFiles(join(Repository.STAGING_DIR, "remove"), Repository.OBJECTS_DIR);
    }

    /**
     * `git rm` command
     */
    public static void rm(String filename) {
        if (!join(Repository.CWD, filename).exists()) {
            return;
        }
        byte[] content = readContents(join(Repository.CWD, filename));
        String hash = sha1(content);
        // staged
        if (Repository.blobMap.containsKey(filename)
                && Repository.blobMap.get(filename).equals(hash)) {
            File file = hashFilename(STAGING_DIR, hash, "add");
            file.delete();
            Repository.blobMap.remove(filename);
            return;
        }
        // tracked
        if (Repository.committedBlobMap.containsKey(filename)
                && Repository.committedBlobMap.get(filename).equals(hash)) {
            File oriFile = hashFilename(OBJECTS_DIR, hash, null);
            File desFile = hashFilename(STAGING_DIR, hash, "remove");
            moveFile(oriFile, desFile);
            Repository.blobMap.put(filename, hash);
            return;
        }
        System.out.println("No reason to remove the file");
    }

    public static void rm(String[] filenameArray) {
        for (String filename : filenameArray) {
            Repository.rm(filename);
        }
    }

    /**
     * `git log` command
     */
    public static void log() {
        String logInfo = generateCommitLog(Repository.currentHead);
        System.out.println(logInfo);
    }

    /**
     * generate commit log iteratively util commit is the commit with message "initial commit"
     */
    private static String generateCommitLog(Commit commit) {
        StringBuilder builder = new StringBuilder();
        while (commit != null) {
            builder.append("===").append('\n');
            builder.append("commit ").append(commit.hash).append('\n');
            if (commit.mergedParentHash != null) {
                Commit parentCommit = readObject(join(COMMIT_DIR, commit.parentHash), Commit.class);
                Commit mergedCommit = readObject(join(COMMIT_DIR, commit.mergedParentHash), Commit.class);
                builder.append("Merge ").append(parentCommit.hash, 0, 7)
                        .append(mergedCommit.hash, 0, 7).append('\n');
            }
            String timeString = convertDateToString(convertStringToDate(commit.timestamp));
            builder.append("Date: ").append(timeString).append('\n');
            builder.append(commit.message).append('\n');
            builder.append('\n');
            if (commit.parentHash == null) {
                commit = null;
                continue;
            }
            commit = readObject(join(COMMIT_DIR, commit.parentHash), Commit.class);
        }
        return builder.toString();
    }

    /**
     * `git global-log` command
     */
    public static void globalLog() {
        List<String> stringList = plainFilenamesIn(Repository.COMMIT_DIR);
        StringBuilder builder = new StringBuilder();
        for (String filename : stringList) {
            Commit commit = readObject(join(COMMIT_DIR, filename), Commit.class);
            builder.append("===").append('\n');
            builder.append("commit ").append(commit.hash).append('\n');
            String timeString = convertDateToString(convertStringToDate(commit.timestamp));
            builder.append("Date: ").append(timeString).append('\n');
            builder.append(commit.message).append('\n');
            builder.append('\n');
        }
        System.out.println(builder.toString());

    }

    /**
     * `git find` command
     */
    public static void find(String message) {
        if (message == null || message.equals("")) {
            return;
        }
        List<String> stringList = plainFilenamesIn(Repository.COMMIT_DIR);
        StringBuilder builder = new StringBuilder();
        for (String filename : stringList) {
            Commit commit = readObject(join(COMMIT_DIR, filename), Commit.class);
            if (!message.equals(commit.message)) {
                continue;
            }
            builder.append(commit.hash).append('\n');
        }
        String result = builder.toString();
        if (result.length() == 0) {
            System.out.println("Found no commit with that message");
            return;
        }
        System.out.println(result);
    }

    /**
     * `git status` command
     */
    public static void status() {
        StringBuilder builder = new StringBuilder();
        builder.append("=== Branches ===").append('\n');
        builder.append("*").append(Repository.currentBranch).append('\n');
        for (String branch : Repository.HEAEDS_DIR.list()) {
            if (branch.equals(Repository.currentBranch)) {
                continue;
            }
            builder.append(branch).append('\n');
        }
        builder.append('\n');

        StringBuilder addedBuilder = new StringBuilder();
        StringBuilder removedBuilder = new StringBuilder();
        for (String filename : Repository.blobMap.keySet()) {
            File addedFile = hashFilename(STAGING_DIR, Repository.blobMap.get(filename), "add");
            File removedFile = hashFilename(STAGING_DIR, Repository.blobMap.get(filename), "remove");
            if (addedFile.exists()) {
                addedBuilder.append(filename).append('\n');
            } else if (removedFile.exists()) {
                removedBuilder.append(filename).append('\n');
            }
        }
        builder.append("=== Staged Files ===").append('\n');
        builder.append(addedBuilder);
        builder.append('\n');

        builder.append("=== Removed Files ===").append('\n');
        builder.append(removedBuilder);
        builder.append('\n');

        builder.append("=== Modifications Not Staged for Commit ===").append('\n');
        //todo
        builder.append('\n');

        builder.append("=== Untracked Files ===").append('\n');
        //todo
        builder.append('\n');

        System.out.println(builder);
    }

    /**
     * `git checkout` command
     */
    public static void checkout(String[] args) {
        if (args[0].equals("--")) {
            Repository.checkout(args[2]);
        } else if (args[1].equals("--")) {
            Repository.checkout(args[0], args[2]);
        } else {
            Repository.checkoutBranch(args[0]);
        }
    }

    public static void checkout(String commitHash, String filename) {
        Commit commit = readObject(join(Repository.COMMIT_DIR, commitHash), Commit.class);
        if (commit == null) {
            System.out.println("No commit with that id exists");
            return;
        }
        if (!commit.blobMap.containsKey(filename)) {
            System.out.println("File does not exist in that commit");
            return;
        }

        String hash = commit.blobMap.get(filename);
        File file = hashFilename(OBJECTS_DIR, hash, null);
        byte[] content = readContents(file);
        writeContents(join(Repository.CWD, filename), content);
    }

    public static void checkout(String filename) {
        checkout(Repository.currentHead.hash, filename);
    }

    public static void checkoutBranch(String branch) {
        if (!join(Repository.HEAEDS_DIR, branch).exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branch.equals(Repository.currentBranch)) {
            System.out.println("No need to checkout the current branch.");
        }
        if (!Repository.blobMap.isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        String commitHash = Repository.readHead(branch);
        Commit commit = readObject(join(Repository.COMMIT_DIR, commitHash), Commit.class);
        HashMap<String, String> tempBlobMap = (HashMap<String, String>) Repository.committedBlobMap.clone();
        for (String filename : commit.blobMap.keySet()) {
            if (tempBlobMap.containsKey(filename)) {
                tempBlobMap.remove(filename);
                if (commit.blobMap.get(filename).equals(tempBlobMap.get(filename))) {
                    continue;
                }
            }
            File file = Repository.hashFilename(Repository.OBJECTS_DIR, commit.blobMap.get(filename), null);
            byte[] content = readContents(file);
            writeContents(join(Repository.CWD, filename), content);
        }
        for (String filename : tempBlobMap.keySet()) {
            join(Repository.CWD, filename).delete();
        }
        Repository.currentBranch = branch;
        Repository.currentHead = commit;
    }

    /**
     * `git branch` command
     */
    public static void branch(String branch) {
        Repository.writeHead(branch, Repository.currentHead.hash);
    }

    /**
     * `git rm-branch` command
     */
    public static void rmBranch(String branch) {
        if (branch.equals(Repository.currentBranch)) {
            System.out.println("Cannot remove the current branch");
            return;
        }
        File file = join(HEAEDS_DIR, branch);
        if (!file.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        file.delete();
    }

    public static String readHead(String branch) {
        return readContentsAsString(join(Repository.HEAEDS_DIR, branch));
    }

    public static void reset(String commitHash) {

    }
}

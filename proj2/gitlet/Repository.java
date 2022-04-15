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

    public static String author = "auto";

    public static String currentBranch = "main";

    public static HashSet<String> branchSet = new HashSet<>();

    public static Commit currentHead;

    /**
     * Key is a filename and Value is the SHA1 code of the file
     */
    public static HashMap<String, String> blobMap = new HashMap<>();

    /**
     * The timestamp of each commit is not a local time but a standard time
     */
    static final String TIME_ZONE = "GMT";

    public static void clear() {
        deleteDirectory(Repository.GITLET_DIR);
    }

    /**
     * `git init` command
     */
    public static void init() {
        String message = "initial commit";
        String timeString = "1970-01-01 08:00:00 +0000";
        String hash = sha1(message, Repository.author, timeString);
        Commit commit = new Commit(message, Repository.author, timeString, hash, null, null, null);
        Repository.currentHead = commit;
        File file = join(COMMIT_DIR, commit.hash);
        writeObject(file, commit);
        Repository.branchSet.add(Repository.currentBranch);
    }

    /**
     * `git add` command
     */
    public static void add(String filename) {
        byte[] content = readContents(join(Repository.CWD, filename));
        String hash = sha1(content);
        File file = hashFilename(OBJECTS_DIR, hash, "add");
        if (file.exists()) {
            return;
        }
        file = hashFilename(STAGING_DIR, hash, "add");
        if (file.exists()) {
            return;
        }
        writeContents(file, content);

        if (Repository.blobMap.containsKey(filename)) {
            hashFilename(STAGING_DIR, Repository.blobMap.get(filename), "add").delete();
        }
        Repository.blobMap.put(filename, hash);
    }

    public static void add(String[] filenameArray) {
        for (String filename : filenameArray) {
            Repository.add(filename);
        }
    }

    private static File hashFilename(File parentDir, String hash, String mode) {
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
        Commit commit = new Commit(message, Repository.author, timeString, hash, parentHash,
                null, Repository.blobMap);
        Repository.currentHead = commit;
        File file = join(COMMIT_DIR, commit.hash);
        writeObject(file, commit);
        Repository.blobMap = new HashMap<>();

        moveDirectory(join(Repository.STAGING_DIR, "add"), Repository.OBJECTS_DIR);
        moveDirectory(join(Repository.STAGING_DIR, "remove"), Repository.OBJECTS_DIR);
    }

    /**
     * `git rm` command
     */
    public static void rm(String filename) {
        if (Repository.blobMap.containsKey(filename)) {
            String hash = Repository.blobMap.get(filename);
            File file = hashFilename(STAGING_DIR, hash, "add");
            file.delete();
            Repository.blobMap.remove(filename);
            return;
        }
        Commit commit = Repository.currentHead;
        while (!(commit.blobMap == null)) {
            if (commit.blobMap.containsKey(filename)) {
                String hash = commit.blobMap.get(filename);
                File oriFile = hashFilename(OBJECTS_DIR, hash, null);
                File desFile = hashFilename(STAGING_DIR, hash, "remove");
                moveDirectory(oriFile.getParentFile(), desFile.getParentFile());
                Repository.blobMap.put(filename, hash);
                return;
            }
            String parentHash = commit.parentHash;
            commit = readObject(join(COMMIT_DIR, parentHash), Commit.class);
        }
        join(CWD, filename).delete();
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
    public static void global_log() {
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
            System.exit(0);
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
        for (String branch : Repository.branchSet) {
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
}

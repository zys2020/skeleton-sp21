package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

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

    public static Commit currentHead;

    /**
     * Key is a filename and Value is the SHA1 code of the file
     */
    public static HashMap<String, String> blobMap = new HashMap<>();

    public static void clear() {
        Path path = GITLET_DIR.toPath();
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * `git init` command
     */
    public static void init() {
        String message = "initial commit";
        Date date = new Date(0);
        String timeString = convertDateToString(date);
        String hash = sha1(message, Repository.author, timeString);
        Commit commit = new Commit(message, Repository.author, timeString, hash, null, null, null);
        Repository.currentHead = commit;
        File file = join(COMMIT_DIR, commit.hash);
        writeObject(file, commit);
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
        if (mode.equals("add")) {
            prefix = "a_";
        } else if (mode.equals("remove")) {
            prefix = "r_";
        } else {
            throw new IllegalArgumentException("Illegal mode `" + mode + "`, only `add` or `remove` is legal.");
        }
        File path = join(parentDir, prefix + hash.substring(0, 2));
        return join(path, hash.substring(2));
    }

    /**
     * `git commit` command
     */
    public static void commit(String message) {
        String timeString = convertDateToString(new Date());
        String hash = sha1(message, Repository.author, timeString);
        String parentHash = Repository.currentHead.hash;
        Commit commit = new Commit(message, Repository.author, timeString, hash, parentHash,
                null, Repository.blobMap);
        Repository.currentHead = commit;
        File file = join(COMMIT_DIR, commit.hash);
        writeObject(file, commit);
        Repository.blobMap = new HashMap<>();

        moveDirectory(Repository.STAGING_DIR, Repository.OBJECTS_DIR);
    }

}

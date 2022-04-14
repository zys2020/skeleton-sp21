package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Date;
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

    public static void clear() {
        Path path = GITLET_DIR.toPath();
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
//                    .peek(System.out::println)
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
        Commit commit = new Commit(message, Repository.author, timeString, hash, null, null);
        Repository.currentHead = commit;
        File file = join(COMMIT_DIR, commit.hash);
        writeObject(file, commit);
    }

}

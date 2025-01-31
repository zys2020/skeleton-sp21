package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

/**
 * Assorted utilities.
 * <p>
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 * @author P. N. Hilfinger, Yuansong Zhang
 */
class Utils {

    /**
     * The length of a complete SHA-1 UID as a hexadecimal numeral.
     */
    static final int UID_LENGTH = 40;

    static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    /* SHA-1 HASH VALUES. */

    /**
     * Returns the SHA-1 hash of the concatenation of VALS, which may
     * be any mixture of byte arrays and Strings.
     */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else if (val != null) {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /**
     * Returns the SHA-1 hash of the concatenation of the strings in
     * VALS.
     */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /**
     * Deletes FILE if it exists and is not a directory.  Returns true
     * if FILE was deleted, and false otherwise.  Refuses to delete FILE
     * and throws IllegalArgumentException unless the directory designated by
     * FILE also contains a directory named .gitlet.
     */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * Deletes the file named FILE if it exists and is not a directory.
     * Returns true if FILE was deleted, and false otherwise.  Refuses
     * to delete FILE and throws IllegalArgumentException unless the
     * directory designated by FILE also contains a directory named .gitlet.
     */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /**
     * Return the entire contents of FILE as a byte array.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return the entire contents of FILE as a String.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /**
     * Write the result of concatenating the bytes in CONTENTS to FILE,
     * creating or overwriting it as needed.  Each object in CONTENTS may be
     * either a String or a byte array.  Throws IllegalArgumentException
     * in case of problems.
     */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("cannot overwrite directory");
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
//                System.out.println("Create new directory `" + file.getParent() + "`");
            }
            BufferedOutputStream str = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     * Throws IllegalArgumentException in case of problems.
     */
    static <T extends Serializable> T readObject(File file, Class<T> expectedClass) {
        try {
            if (!file.exists()) {
                return null;
            }
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Write OBJ to FILE.
     */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* DIRECTORIES */

    /**
     * Filter out all but plain files.
     */
    private static final FilenameFilter PLAIN_FILES =
            new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isFile();
                }
            };

    /**
     * Filter out all but plain directory.
     */
    private static final FilenameFilter PLAIN_DIRECTORY =
            new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isDirectory();
                }
            };

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     * method.
     */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     * method.
     */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /**
     * Returns a byte array containing the serialized contents of OBJ.
     */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }

    /* MESSAGES AND ERROR REPORTING */

    /**
     * Return a GitletException whose message is composed from MSG and ARGS as
     * for the String.format method.
     */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /**
     * Print a message composed from MSG and ARGS as for the String.format
     * method, followed by a newline.
     */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }

    /**
     * convert timestamp String to timestamp Date
     */
    static Date convertStringToDate(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        try {
            return dateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * convert timestamp Date to timestamp String that is a local time
     */
    static String convertDateToString(Date date) {
        return convertDateToString(date, TIME_FORMAT, null);

    }

    static String convertDateToString(Date date, String timeZone) {
        return convertDateToString(date, TIME_FORMAT, timeZone);

    }

    static String convertDateToString(Date date, String format, String timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        if (timeZone != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return dateFormat.format(date);

    }

    /**
     * move the directory oriDir to the directory desDir
     */
    static void moveDirectory(File oriDir, File desDir) {
        if (!oriDir.exists()) {
            return;
        }
        try {
            if (!desDir.getParentFile().exists()) {
                desDir.getParentFile().mkdirs();
//                System.out.println("Create new directory `" + desDir.getParent() + "`");
            }
            FileUtils.copyDirectory(oriDir, desDir);
            deleteDirectory(oriDir);
//             DirectoryNotEmptyException will throw.
//            Files.move(oriDir.toPath(), desDir.toPath(), REPLACE_EXISTING);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("moving failed");
        }
    }

    /**
     * move the file "oriFile" to the file "desFile"
     */
    static void moveFile(File oriFile, File desFile) {
        if (!oriFile.exists()) {
            return;
        }
        try {
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
//                System.out.println("Create new directory `" + desDir.getParent() + "`");
            }
            Files.move(oriFile.toPath(), desFile.toPath());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("moving failed");
        }
    }

    /**
     * copy the file "oriFile" to the file "desFile"
     */
    static void copyFile(File oriFile, File desFile) {
        if (!oriFile.exists()) {
            return;
        }
        try {
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
//                System.out.println("Create new directory `" + desDir.getParent() + "`");
            }
            Files.copy(oriFile.toPath(), desFile.toPath());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("copy failed");
        }
    }

    /**
     * delete "dir" directory with the constraint that Repository.CWD cannot be deleted.
     */
    static void deleteDirectory(File dir) {
        Path path = dir.toPath();
        if (!dir.exists()) {
            return;
        }
        if (Repository.CWD.toString().contains(dir.toString())) {
            System.out.println("cannot delete the current work directory");
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
     * delete files in the "desDir" directory that also exist in the "oriDir" directory.
     * and the search depth is two.
     */
    static void deleteFiles(File oriDir, File desDir) {
        if (!oriDir.exists() || !desDir.exists()) {
            return;
        }
        String[] indexDirs = oriDir.list(PLAIN_DIRECTORY);
        for (String index : indexDirs) {
            String[] filenames = new File(oriDir, index).list(PLAIN_FILES);
            for (String filename : filenames) {
                File path = new File(desDir, index);
                File file = new File(path, filename);
                if (file.exists()) {
                    file.delete();
                    path = new File(oriDir, index);
                    file = new File(path, filename);
                    file.delete();
                }
            }
        }
    }

    public static void main(String[] args) {
//        Date date = convertStringToDate("2022-04-14 12:00:00 +0000");
//        System.out.println(date);
//        String timeString = convertDateToString(new Date());
//        System.out.println(timeString);
//        deleteDirectory(Repository.CWD);
        deleteFiles(join(Repository.STAGING_DIR, "add"), join(Repository.STAGING_DIR, "add"));
    }
}

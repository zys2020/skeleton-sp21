package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a gitlet commit object.
 *
 * @author Yuansong Zhang
 */
public class Commit implements Serializable {

    /**
     * The message of this Commit.
     */
    public final String message;

    /**
     * The author of this Commit
     */
    public final String author;

    /**
     * committing timestamp
     */
    public final String timestamp;

    /**
     * SHA1 of this Commit
     */
    public final String hash;

    /**
     * SHA1 of the parent commit of this Commit
     */
    public final String parentHash;

    /**
     * SHA1 of the merged parent commit of this Commit
     */
    public final String mergedParentHash;

    /**
     * This is blob map from a filename (key) to SHA1 code (value) of the file
     */
    public final HashMap<String, String> blobMap;

    public Commit(String message, String author, String timestamp, String hash, String parentHash,
                  String mergedParentHash, HashMap<String, String> blobMap) {
        this.message = message;
        this.author = author;
        this.timestamp = timestamp;
        this.hash = hash;
        this.parentHash = parentHash;
        this.mergedParentHash = mergedParentHash;
        this.blobMap = blobMap;
    }
}

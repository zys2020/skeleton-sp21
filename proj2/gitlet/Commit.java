package gitlet;

import java.io.Serializable;

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

    public Commit(String message, String author, String timestamp, String hash, String parentHash, String mergedParentHash) {
        this.message = message;
        this.author = author;
        this.timestamp = timestamp;
        this.hash = hash;
        this.parentHash = parentHash;
        this.mergedParentHash = mergedParentHash;
    }
}

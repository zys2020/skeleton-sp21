package gitlet;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

import static gitlet.Utils.*;

public class CommandTest {
    @Test
    public void initTest() {
        Repository.clear();
        assertFalse(Repository.GITLET_DIR.exists());
        Repository.init();
        assertTrue(Repository.GITLET_DIR.exists());
        Repository.init();
    }

    @Test
    public void addTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        assertEquals(1, Repository.blobMap.size());
        File file = Repository.hashFilename(Repository.STAGING_DIR, sha1(content), "add");
        assertEquals(content, readContentsAsString(file));
        filename = "swaewwaeg.unexisted";
        Repository.add(filename);
        assertEquals(1, Repository.blobMap.size());
    }

    @Test
    public void commitTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Test commit 1");
    }

    @Test
    public void integrationTest() {
        // add

    }

}

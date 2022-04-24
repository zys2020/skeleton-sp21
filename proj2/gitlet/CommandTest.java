package gitlet;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;

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
        assertEquals(0, Repository.STAGING_DIR.list().length);
    }

    @Test
    public void rmTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Test commit 1");
        String hash = Repository.committedBlobMap.get(filename);
        File file = Repository.hashFilename(Repository.OBJECTS_DIR, hash, null);
        Repository.rm(filename);
        assertFalse(file.exists());
        file = Repository.hashFilename(Repository.STAGING_DIR, hash, "remove");
        assertTrue(file.exists());
        assertTrue(Repository.blobMap.containsKey(filename));

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        hash = Repository.blobMap.get(filename);
        file = Repository.hashFilename(Repository.STAGING_DIR, hash, "add");
        Repository.rm(filename);
        assertFalse(file.exists());
        assertFalse(Repository.blobMap.containsKey(filename));

        filename = "file3.txt";
        content = "file3";
        writeContents(join(Repository.CWD, filename), content);
        Repository.rm(filename);
        assertTrue(join(Repository.CWD, filename).exists());
    }

    @Test
    public void logTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        Repository.log();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.rm(filename);
        Repository.commit("Delete " + filename);
        Repository.log();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        Repository.log();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    }

    @Test
    public void globalLogTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        Repository.globalLog();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.rm(filename);
        Repository.commit("Delete " + filename);
        Repository.globalLog();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        Repository.globalLog();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    }

    @Test
    public void findTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        Repository.rm(filename);
        Repository.commit("Delete " + filename);

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        Repository.log();
        Repository.find("Add " + filename);
    }

    @Test
    public void statusTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.add(filename);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.commit("Add " + filename);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.rm(filename);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.commit("Delete " + filename);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.commit("Add " + filename);
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Repository.status();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    }

    @Test
    public void checkoutTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String commitHash = Repository.currentHead.hash;

        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Modify " + filename);

        content = "file3";
        writeContents(join(Repository.CWD, filename), content);
        assertEquals(content, readContentsAsString(join(Repository.CWD, filename)));

        Repository.checkout(filename);
        assertEquals("file2", readContentsAsString(join(Repository.CWD, filename)));

        Repository.checkout(commitHash, filename);
        assertEquals("file1", readContentsAsString(join(Repository.CWD, filename)));

        Repository.status();
        //todo checkout branch
    }

    @Test
    public void branchTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String commitHash = Repository.currentHead.hash;
        String branch = "dev";
        Repository.branch(branch);
        assertEquals(commitHash, Repository.readHead(branch));

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        assertEquals(commitHash, Repository.readHead(branch));
        assertNotEquals(Repository.currentHead.hash, Repository.readHead(branch));
        branch = "hi";
        Repository.branch(branch);
        assertEquals(Repository.currentHead.hash, Repository.readHead(branch));
    }

    @Test
    public void rmBranchTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String commitHash = Repository.currentHead.hash;
        String branch = "dev";
        Repository.branch(branch);

        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        assertEquals(commitHash, Repository.readHead(branch));
        branch = "hi";
        Repository.branch(branch);
        assertTrue(join(Repository.HEAEDS_DIR, branch).exists());
        Repository.rmBranch(branch);
        assertFalse(join(Repository.HEAEDS_DIR, branch).exists());
    }

    @Test
    public void checkoutBranchTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);

        String branch = "dev";
        Repository.branch(branch);
        Repository.checkoutBranch(branch);

        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Modify " + filename);

        content = "file3";
        writeContents(join(Repository.CWD, filename), content);
        branch = "hi";
        Repository.branch(branch);
        Repository.checkoutBranch(branch);
        Repository.add(filename);
        Repository.commit("Modify " + filename);

        Repository.checkoutBranch("main");
        assertEquals("file1", readContentsAsString(join(Repository.CWD, filename)));
    }

    @Test
    public void resetTest() {
        Repository.clear();
        Repository.init();
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String commitHash = Repository.currentHead.hash;

        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Modify " + filename);

        content = "file3";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Modify " + filename);

        Repository.reset(commitHash);
        assertEquals("file1", readContentsAsString(join(Repository.CWD, filename)));
    }

    @Test
    public void mergeTest() {
        Repository.clear();
        Repository.init();

        // The file does not exist in both the current branch and given branch.
        String filename = "file1.txt";
        String content = "file1";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String filename1 = filename;
        String content1 = content;

        // The file exists in the current branch but not in the given branch.
        filename = "file2.txt";
        content = "file2";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String filename2 = filename;
        String content2 = content;

        // The file exists in the given branch but not in the current branch.
        filename = "file3.txt";
        content = "file3";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String filename3 = filename;
        String content3 = content;

        // The file exists in both the current branch and given branch.
        filename = "file4.txt";
        content = "file4";
        writeContents(join(Repository.CWD, filename), content);
        Repository.add(filename);
        Repository.commit("Add " + filename);
        String filename4 = filename;
        String content4 = content;

        // given branch
        String branch = "dev";
        Repository.branch(branch);
        Repository.checkoutBranch(branch);

        Repository.rm(filename1);
        Repository.commit("Remove " + filename1);

        Repository.rm(filename2);
        Repository.commit("Remove " + filename2);

        writeContents(join(Repository.CWD, filename3), filename3);
        Repository.add(filename3);
        Repository.commit("Modify " + filename3);

        writeContents(join(Repository.CWD, filename4), filename4);
        Repository.add(filename4);
        Repository.commit("Modify " + filename4);

//        Repository.log();

        // current branch
        Repository.checkoutBranch("main");

        Repository.rm(filename1);
        Repository.commit("Remove " + filename1);

        writeContents(join(Repository.CWD, filename2), filename2);
        Repository.add(filename2);
        Repository.commit("Modify " + filename2);

        Repository.rm(filename3);
        Repository.commit("Remove " + filename3);

        writeContents(join(Repository.CWD, filename4), filename4);
        Repository.add(filename4);
        Repository.commit("Modify " + filename4);

//        Repository.log();

        // merge given branch whose existed files are the same with current branch.
        Repository.merge(branch);
        assertFalse(join(Repository.CWD, filename1).exists());
        assertEquals(readContentsAsString(join(Repository.CWD, filename2)), filename2);
        assertNotEquals(readContentsAsString(join(Repository.CWD, filename3)), filename3);
    }

    @Test
    public void integrationTest() {
        // add
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "a");
        HashMap<String, String> map1 = (HashMap<String, String>) map.clone();

    }

}

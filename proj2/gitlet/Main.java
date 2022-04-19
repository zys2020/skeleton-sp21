package gitlet;

import java.util.Arrays;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Yuansong Zhang
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        Repository.clear();
        Repository.init();
////        Repository.status();
//        Repository.add("file1.txt");
////        Repository.status();
//        Repository.add("file2.txt");
////        Repository.status();
//        Repository.commit("Test commit 1");
////        Repository.status();
//        Repository.add("file3.txt");
////        Repository.status();
//        Repository.rm("file3.txt");
////        Repository.status();
//        Repository.rm("file2.txt");
////        Repository.status();
//        Repository.rm("file4.txt");
////        Repository.status();
//        Repository.commit("Delete file2.txt");
////        Repository.status();
////        Repository.log();
////        Repository.global_log();
//        Repository.find("Test commit 1");
////        Repository.checkout("file1.txt");
//        Repository.checkout("file2.txt");
//        Repository.branch("dev");
//        Repository.rm_branch("dev");
        // TODO: what if args is empty?
        if (args.length == 0) {
            // help info
            return;
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.add(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "commit":
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.rm(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.global_log();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rm_branch(args[1]);
                break;
            case "reset":
                break;
            case "merge":
                break;

        }
    }
}

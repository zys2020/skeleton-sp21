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
        Repository.add("file1.txt");
        Repository.add("file2.txt");
        Repository.commit("A test commit");
        Repository.add("file3.txt");
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
                break;
        }
    }
}

package capers;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/**
 * A repository for Capers
 *
 * @author Yuansong Zhang
 * The structure of a Capers Repository is as follows:
 * <p>
 * .capers/ -- top level folder for all persistent data in your lab6 folder
 * <p>
 * - dogs/ -- folder containing all of the persistent data for dogs
 * <p>
 * - story -- file containing the current story
 * <p>
 */
public class CapersRepository {
    /**
     * Current Working Directory.
     */
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Main metadata folder.
     */
    static final File CAPERS_FOLDER = join(CWD, "capers");

    static final String STORY_NAME = "story.txt";

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     * <p>
     * .capers/ -- top level folder for all persistent data in your lab6 folder
     * - dogs/ -- folder containing all the persistent data for dogs
     * - story -- file containing the current story
     */
    public static void setupPersistence() {
        if (!CapersRepository.CAPERS_FOLDER.exists()) {
            CapersRepository.CAPERS_FOLDER.mkdirs();
        }
        if (!Dog.DOG_FOLDER.exists()) {
            Dog.DOG_FOLDER.mkdirs();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     *
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        File file = join(CAPERS_FOLDER, STORY_NAME);
        String contents = readContentsAsString(file);
        if (contents != null) {
            System.out.print(contents);
            contents = contents + text + "\n";
        } else {
            contents = text + "\n";
        }
        writeContents(file, contents);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     *
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        Dog dog = Dog.fromFile(name);
        dog.haveBirthday();
    }
}

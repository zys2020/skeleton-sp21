package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    public TERenderer ter = new TERenderer();

    /* Feel free to change the width and height. */
    public static final int WIDTH = 16 * 4;
    public static final int HEIGHT = 9 * 4;
    private static final long SEED = System.currentTimeMillis();
    public static final Random RANDOM = new Random(SEED);
    public static final String gameFilename = "game.txt";

    public int avatar_x = -1;
    public int avatar_y = -1;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource = new KeyboardInputSource();
        int totalCharacters = 0;
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        int avatar_x = -1;
        int avatar_y = -1;

        while (inputSource.possibleNextInput()) {
            totalCharacters += 1;
            char c = inputSource.getNextKey();
            if (c == 'N') {
                System.out.println("Create New World.");
                world = interactWithInputString("XXXX");
            } else if (c == 'W') {
                avatar_y = this.avatar_y + 1;
                avatar_x = this.avatar_x;

            } else if (c == 'S') {
                avatar_y = this.avatar_y - 1;
                avatar_x = this.avatar_x;

            } else if (c == 'A') {
                avatar_x = this.avatar_x - 1;
                avatar_y = this.avatar_y;

            } else if (c == 'D') {
                avatar_x = this.avatar_x + 1;
                avatar_y = this.avatar_y;

            } else if (c == 'L') {
                int[] avatar = this.loadGame(world);
                if (avatar == null) {
                    continue;
                }
                avatar_x = avatar[0];
                avatar_y = avatar[1];
            } else if (c == 'Q') {
                this.saveGame(world);
                System.out.println("Quit and Save Game...");
                break;
            }
            this.render(world, avatar_x, avatar_y);
        }

        System.out.println("Processed " + totalCharacters + " characters.");
    }

    private void saveGame(TETile[][] world) {
        File file = new File(gameFilename);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                content.append(world[i][j].description()).append(",");
            }
            content.append("\n");
        }
        try {
            Files.writeString(file.toPath(), content.toString());
        } catch (IOException e) {
            System.out.println("file write exception.");
        }
    }

    private int[] loadGame(TETile[][] world) {
        File file = new File(gameFilename);
        if (!file.exists()) {
            System.out.println("Game file is not existed.");
            return null;
        }
        int x = -1;
        int y = -1;
        try {
            List<String> content = Files.readAllLines(file.toPath());
            for (int i = 0; i < content.size(); i++) {
                String[] s = content.get(i).split(",");
                for (int j = 0; j < s.length; j++) {
                    switch (s[j]) {
                        case "nothing":
                            world[i][j] = Tileset.NOTHING;
                            break;
                        case "floor":
                            world[i][j] = Tileset.FLOOR;
                            break;
                        case "wall":
                            world[i][j] = Tileset.WALL;
                            break;
                        case "avatar":
                            world[i][j] = Tileset.AVATAR;
                            x = i;
                            y = j;
                            this.avatar_x = x;
                            this.avatar_y = y;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("load file exception");
        }
        return new int[]{x, y};
    }

    private static int calIndex(int x, int y, TETile[][] world) {
        return x + y * world[0].length;
    }

    /**
     * https://www.redblobgames.com/maps/terrain-from-noise/
     * http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/
     */
    private static void generateSpanningTree(TETile[][] world, int init_x, int init_y) {
        ArrayList<Integer[]> spanningTree = new ArrayList<>();
        for (int i = 0; i < world.length * world[0].length; i++) {
            spanningTree.add(new Integer[]{-1, -1});
        }
        int x = init_x;
        int y = init_y;
        world[x][y] = Tileset.FLOOR;
        for (int i = 0; i < 100; i++) {
            int index = calIndex(x, y, world);
            int temp_x = -1;
            int temp_y = -1;
            int[] directionList = new int[]{0, 1, 2, 3};
            RandomUtils.shuffle(RANDOM, directionList);
            for (int k : directionList) {
                temp_x = x;
                temp_y = y;
                switch (k) {
                    case 0:
                        // up
                        temp_y += 1;
                        break;
                    case 1:
                        // right
                        temp_x += 1;
                        break;
                    case 2:
                        // down
                        temp_y -= 1;
                        break;
                    case 3:
                        // left
                        temp_x -= 1;
                        break;
                }
                // valid judgement
                if ((temp_x < 0) || (temp_x > world.length) || (temp_y < 0) || (temp_y > world[0].length)) {
                    continue;
                }
                int childIndex = calIndex(temp_x, temp_y, world);
                Integer[] item = spanningTree.get(childIndex);
                if ((item[0] != -1) || (item[1] != -1)) {
                    continue;
                }
                item[0] = index;
                item = spanningTree.get(index);
                item[1] = childIndex;

                world[x][y] = Tileset.FLOOR;
                x = temp_x;
                y = temp_y;
                break;
            }
            if ((temp_x == -1) || (temp_y == -1) || (temp_x != x) || (temp_y != y)) {
                break;
            }
        }
    }

    private void generateRoom(TETile[][] world, int init_x, int init_y) {
        Room rectangularRoom = new RectangularRoom(init_x, init_y, 2, world);
        Room initRoom = rectangularRoom;
        rectangularRoom.fillWithFloors(world);
        rectangularRoom.fillWithWalls(world);
        Room hallway = new HallWay(
                initRoom.getOutputCoordinate()[0],
                initRoom.getOutputCoordinate()[1],
                RANDOM.nextInt(9) + 1,
                world);
        hallway.fillWithFloors(world);
        hallway.fillWithWalls(world);
        if (rectangularRoom.getStatus() || hallway.getStatus()) {
            return;
        }
        this.avatar_x = rectangularRoom.getInputCoordinate()[0];
        this.avatar_y = rectangularRoom.getInputCoordinate()[1];

        for (int i = 0; i < 200; i++) {
            rectangularRoom = new RectangularRoom(
                    hallway.getOutputCoordinate()[0],
                    hallway.getOutputCoordinate()[1],
                    RANDOM.nextInt(3) + 2,
                    world);
//            System.out.println("room:" + rectangularRoom.info());
            if (rectangularRoom.getStatus()) {
                break;
            }
            rectangularRoom.fillWithFloors(world);
            rectangularRoom.fillWithWalls(world);

            hallway = new HallWay(
                    rectangularRoom.getOutputCoordinate()[0],
                    rectangularRoom.getOutputCoordinate()[1],
                    RANDOM.nextInt(9) + 1,
                    world);
//            System.out.println("hallway:" + hallway.info());
            if (hallway.getStatus()) {
                break;
            }
            hallway.fillWithFloors(world);
            hallway.fillWithWalls(world);
        }

        hallway = new HallWay(
                initRoom.getInputCoordinate()[0],
                initRoom.getInputCoordinate()[1],
                RANDOM.nextInt(9) + 1,
                world);
        hallway.fillWithFloors(world);
        hallway.fillWithWalls(world);
        if (rectangularRoom.getStatus() || hallway.getStatus()) {
            return;
        }

        for (int i = 0; i < 200; i++) {
            rectangularRoom = new RectangularRoom(
                    hallway.getOutputCoordinate()[0],
                    hallway.getOutputCoordinate()[1],
                    RANDOM.nextInt(3) + 2,
                    world);
//            System.out.println("room:" + rectangularRoom.info());
            if (rectangularRoom.getStatus()) {
                break;
            }
            rectangularRoom.fillWithFloors(world);
            rectangularRoom.fillWithWalls(world);

            hallway = new HallWay(
                    rectangularRoom.getOutputCoordinate()[0],
                    rectangularRoom.getOutputCoordinate()[1],
                    RANDOM.nextInt(9) + 1,
                    world);
//            System.out.println("hallway:" + hallway.info());
            if (hallway.getStatus()) {
                break;
            }
            hallway.fillWithFloors(world);
            hallway.fillWithWalls(world);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // initialize the tile rendering engine with a window of size width x height

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int init_x = RANDOM.nextInt(WIDTH - 2) + 1;
        int init_y = RANDOM.nextInt(HEIGHT - 2) + 1;
//        generateSpanningTree(world, init_x, init_y);
        generateRoom(world, init_x, init_y);
        return world;
    }

    public void render(TETile[][] world, int avatar_x, int avatar_y) {
        if (world == null) {
            return;
        }
        if (!this.ter.init) {
            this.ter.initialize(WIDTH, HEIGHT);
        }
        world[this.avatar_x][this.avatar_y] = Tileset.AVATAR;
        if ((avatar_x >= 0) &&
                (avatar_x < world.length) &&
                (avatar_y >= 0) &&
                (avatar_y < world[0].length) &&
                (world[avatar_x][avatar_y] == Tileset.FLOOR)) {
            world[avatar_x][avatar_y] = Tileset.AVATAR;
            world[this.avatar_x][this.avatar_y] = Tileset.FLOOR;
            this.avatar_x = avatar_x;
            this.avatar_y = avatar_y;
        }
        this.ter.renderFrame(world);
    }
}

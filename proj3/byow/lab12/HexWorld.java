package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    // private static final long SEED = System.currentTimeMillis();
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void generateHexGrid(TETile[][] world, int size, int init_x, int init_y) {
        int width = size + (size - 1) * 2;
        int height = size * 2;
        TETile tileType = randomTile();

        // one half of hexagonal grid
        for (int y = 0; y < height * 0.5; y += 1) {
            for (int x = size - 1 - y; x <= width - size + y; x += 1) {
                world[x + init_x][y + init_y] = tileType;
            }
        }

        // another half of hexagonal grid
        for (int y = height / 2; y < height; y += 1) {
            for (int x = y - height / 2; x < width - y + height / 2; x += 1) {
                world[x + init_x][y + init_y] = tileType;
            }
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.GRASS;
            case 3:
                return Tileset.AVATAR;
            case 4:
                return Tileset.MOUNTAIN;
            default:
                return Tileset.WALL;
        }
    }

    private static int[] neighbour(int hexSize, int x, int y, int direction) {
        int[] coordinates = new int[2];
        coordinates[0] = x;
        coordinates[1] = y;
        int width = hexSize + (hexSize - 1) * 2;
        int height = hexSize * 2;
        switch (direction) {
            case 0:
                // up
                coordinates[1] = y + height;
                break;
            case 1:
                // right up
                coordinates[0] = x + (width - hexSize) / 2 + hexSize;
                coordinates[1] = y + (height - 2) / 2 + 2 - 1;
                break;
            case 2:
                // right down
                coordinates[0] = x + (width - hexSize) / 2 + hexSize;
                coordinates[1] = y - ((height - 2) / 2 + 2 - 1);
                break;
            case 3:
                // down
                coordinates[1] = y - height;
                break;
            case 4:
                // left down
                coordinates[0] = x - ((width - hexSize) / 2 + hexSize);
                coordinates[1] = y - ((height - 2) / 2 + 2 - 1);
                break;
            case 5:
                // left up
                coordinates[0] = x - ((width - hexSize) / 2 + hexSize);
                coordinates[1] = y + ((height - 2) / 2 + 2 - 1);
                break;
        }
        return coordinates;
    }

    public static void neighbourTest() {
        // initialize the tile rendering engine with a window of size width x height
        TERenderer ter = new TERenderer();
        int n = 4;
        int width = 16 * n;
        int height = 9 * n;
        ter.initialize(width, height);
        // initialize tiles
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int hexSize = 3;
//        int init_x = RANDOM.nextInt(width);
//        int init_y = RANDOM.nextInt(height);
        int init_x = 0;
        int init_y = 0;
        generateHexGrid(world, hexSize, init_x, init_y);
        int[] coordinates;
        coordinates = neighbour(hexSize, init_x, init_y, 0);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, init_x, init_y, 1);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 1);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 1);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 1);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 1);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 3);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 3);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 4);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        coordinates = neighbour(hexSize, coordinates[0], coordinates[1], 5);
        generateHexGrid(world, hexSize, coordinates[0], coordinates[1]);
        // draws the world to the screen
        ter.renderFrame(world);
    }

    private static boolean isValid(int x, int y, int hexSize, int windowWidth, int windowHeight) {
        int width = hexSize + (hexSize - 1) * 2;
        int height = hexSize * 2;
        return (0 <= x) & (x < windowWidth - width) & (0 <= y) & (y < windowHeight - height);
    }

    public static void randomGenerateTest() {
        // initialize the tile rendering engine with a window of size width x height
        TERenderer ter = new TERenderer();
        int n = 4;
        int windowWidth = 16 * n;
        int windowHeight = 9 * n;
        ter.initialize(windowWidth, windowHeight);
        // initialize tiles
        TETile[][] world = new TETile[windowWidth][windowHeight];
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int hexSize = 3;
        int width = hexSize + (hexSize - 1) * 2;
        int height = hexSize * 2;
        int init_x = RANDOM.nextInt(windowWidth - width);
        int init_y = RANDOM.nextInt(windowHeight - height);
        generateHexGrid(world, hexSize, init_x, init_y);
        int[] coordinates = new int[]{init_x, init_y};
        int num = 0;
        while (num < 10) {
            System.out.print(coordinates[0]);
            System.out.println(coordinates[1]);
            int[] temp_coordinates = neighbour(hexSize, coordinates[0], coordinates[1], RANDOM.nextInt(6));
            if (isValid(temp_coordinates[0], temp_coordinates[1], hexSize, windowWidth, windowHeight)) {
                generateHexGrid(world, hexSize, temp_coordinates[0], temp_coordinates[1]);
                coordinates = temp_coordinates;
                num += 1;
            }
        }
        // draws the world to the screen
        ter.renderFrame(world);
    }

    public static void main(String[] args) {
//        neighbourTest();
        randomGenerateTest();
    }
}

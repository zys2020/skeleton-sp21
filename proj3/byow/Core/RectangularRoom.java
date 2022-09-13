package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;

public class RectangularRoom implements Room {
    public int x = -1;
    public int y = -1;
    public int width;
    public int input_x;
    public int input_y;
    public int output_x;
    public int output_y;
    public boolean stop = false;

    RectangularRoom(int input_x, int input_y, int width, TETile[][] world) {
        // bottom left coordinate
        this.input_x = input_x;
        this.input_y = input_y;
        this.width = width;
        int[] sample = new int[]{0, 1, 2, 3};
        RandomUtils.shuffle(Engine.RANDOM, sample);
        int index = 0;
        while (!isValid(world) & index < 4) {
            int[] offset = new int[this.width];
            for (int i = 0; i < this.width; i++) {
                offset[i] = i;
            }
            RandomUtils.shuffle(Engine.RANDOM, offset);
            for (int n : offset) {
                switch (sample[index]) {
                    case 0:
                        // left
                        this.x = input_x - width;
                        this.y = input_y - n;
                        break;
                    case 1:
                        // right
                        this.x = input_x + 1;
                        this.y = input_y - n;
                        break;
                    case 2:
                        // upper
                        this.x = input_x - n;
                        this.y = input_y + 1;
                        break;
                    case 3:
                        // lower
                        this.x = input_x - n;
                        this.y = input_y - width;
                        break;
                }
                if (isValid(world)) {
                    break;
                }
            }
            index += 1;
        }

        if ((index >= 4) & (!isValid(world))) {
            this.stop = true;
        }

        int[] boundary = this.getBoundary();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = boundary[0]; i <= boundary[1]; i++) {
            arrayList.add(i + "," + (boundary[2] - 1));
            arrayList.add(i + "," + (boundary[3] + 1));
        }
        for (int i = boundary[2]; i <= boundary[3]; i++) {
            arrayList.add((boundary[0] - 1) + "," + i);
            arrayList.add((boundary[1] + 1) + "," + i);
        }

        int output_index = Engine.RANDOM.nextInt(arrayList.size());
        String[] s = arrayList.get(output_index).split(",");
        int output_x = Integer.parseInt(s[0]);
        int output_y = Integer.parseInt(s[1]);
        while ((Math.abs(output_x - input_x) + Math.abs(output_y - input_y)) < 2) {
            output_index = Engine.RANDOM.nextInt(arrayList.size());
            s = arrayList.get(output_index).split(",");
            output_x = Integer.parseInt(s[0]);
            output_y = Integer.parseInt(s[1]);
        }
        this.output_x = output_x;
        this.output_y = output_y;
    }

    public String info() {
        return x + "," + y + "," + width;
    }

    public boolean getStatus() {
        return this.stop;
    }

    public int[] getBoundary() {
        // min x, max x, min y, max y
        int[] boundary = new int[4];
        boundary[0] = this.x;
        boundary[1] = this.x + this.width - 1;
        boundary[2] = this.y;
        boundary[3] = this.y + this.width - 1;
        return boundary;
    }

    public ArrayList<int[]> getFloor() {
        ArrayList<int[]> items = new ArrayList<>();
        int[] boundary = getBoundary();
        for (int x = boundary[0]; x <= boundary[1]; x += 1) {
            for (int y = boundary[2]; y <= boundary[3]; y += 1) {
                items.add(new int[]{x, y});
            }
        }
        return items;
    }

    public ArrayList<int[]> getWall() {
        ArrayList<int[]> items = new ArrayList<>();
        int[] boundary = getBoundary();
        int x, y;
        x = boundary[0] - 1;
        for (y = boundary[2] - 1; y <= boundary[3] + 1; y += 1) {
            items.add(new int[]{x, y});
        }
        x = boundary[1] + 1;
        for (y = boundary[2] - 1; y <= boundary[3] + 1; y += 1) {
            items.add(new int[]{x, y});
        }
        y = boundary[2] - 1;
        for (x = boundary[0]; x <= boundary[1]; x += 1) {
            items.add(new int[]{x, y});
        }
        y = boundary[3] + 1;
        for (x = boundary[0]; x <= boundary[1]; x += 1) {
            items.add(new int[]{x, y});
        }
        return items;
    }

    public int[] getInputCoordinate() {
        return new int[]{input_x, input_y};
    }

    public int[] getOutputCoordinate() {
        return new int[]{output_x, output_y};
    }

    public void fillWithFloors(TETile[][] world) {
        ArrayList<int[]> items = getFloor();
        for (int[] item : items) {
            if (world[item[0]][item[1]] == Tileset.NOTHING) {
                world[item[0]][item[1]] = Tileset.FLOOR;
            }
        }
        world[this.input_x][this.input_y] = Tileset.FLOOR;
        world[this.output_x][this.output_y] = Tileset.FLOOR;
    }

    private boolean isValid(TETile[][] world) {
        if (this.x == -1 || this.y == -1) {
            return false;
        }
        ArrayList<int[]> items = getFloor();
        for (int[] item : items) {
            if ((item[0] < 0) || (item[0] >= world.length) || (item[1] < 0) || (item[1] >= world[0].length)) {
                return false;
            }
            if (world[item[0]][item[1]] != Tileset.NOTHING) {
                return false;
            }
        }
        items = getWall();
        for (int[] item : items) {
            if ((item[0] < 0) || (item[0] >= world.length) || (item[1] < 0) || (item[1] >= world[0].length)) {
                return false;
            }
            if ((item[0] == this.input_x) & ((item[1] == this.input_y))) {
                continue;
            }
            if ((world[item[0]][item[1]] != Tileset.NOTHING) & (world[item[0]][item[1]] != Tileset.WALL)) {
                return false;
            }
        }
        return true;
    }

    public void fillWithWalls(TETile[][] world) {
        ArrayList<int[]> items = getWall();
        for (int[] item : items) {
            if (world[item[0]][item[1]] == Tileset.NOTHING) {
                world[item[0]][item[1]] = Tileset.WALL;
            }
        }
    }
}

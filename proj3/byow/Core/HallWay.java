package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class HallWay implements Room {
    public ArrayList<int[]> arrayList;
    public int length;
    public boolean stop = false;

    private static boolean arrayContains(ArrayList<int[]> arrayList, int[] item) {
        for (int[] tempItem : arrayList) {
            if (Arrays.equals(item, tempItem)) {
                return true;
            }
        }
        return false;
    }

    HallWay(int x, int y, int length, TETile[][] world) {
        this.arrayList = new ArrayList<>();
        this.arrayList.add(new int[]{x, y});
        this.length = length;

        int[] currentItem = this.arrayList.get(0);
        world[currentItem[0]][currentItem[1]] = Tileset.FLOOR;

        for (int i = 0; i < length; i++) {
            currentItem = this.arrayList.get(this.arrayList.size() - 1);
            ArrayList<int[]> neighbourItems = HallWay.getNeighbourItem(currentItem);
            int[] sample = new int[]{0, 1, 2, 3};
            RandomUtils.shuffle(Engine.RANDOM, sample);
            for (int j : sample) {
                int[] childItem = neighbourItems.get(j);
                if (!arrayContains(this.arrayList, childItem) &&
                        (childItem[0] > 0) &&
                        (childItem[0] < world.length) &&
                        (childItem[1] > 0) &&
                        (childItem[1] < world[0].length) &&
                        (world[childItem[0]][childItem[1]] == Tileset.NOTHING)) {
                    ArrayList<int[]> tempNeighbourItems = HallWay.getNeighbourItem(childItem);
                    int marker = 0;
                    for (int[] item : tempNeighbourItems) {
                        if (arrayContains(this.arrayList, item) ||
                                !((item[0] > 0) &&
                                        (item[0] < world.length) &&
                                        (item[1] > 0) &&
                                        (item[1] < world[0].length)) ||
                                (world[item[0]][item[1]] == Tileset.FLOOR)) {
                            marker += 1;
                        }
                    }
                    if (marker < 2) {
                        this.arrayList.add(childItem);
                        break;
                    }
                }
            }
        }
        if (this.arrayList.size() < 2) {
            this.stop = true;
        }
    }

    public boolean getStatus() {
        return this.stop;
    }

    public String info() {
        return arrayList.get(0)[0] + "," + arrayList.get(0)[1] + "," + length;
    }

    private static ArrayList<int[]> getNeighbourItem(int[] currentItem) {
        ArrayList<int[]> arrayList = new ArrayList<>();
        int[] leftItem = new int[]{currentItem[0] - 1, currentItem[1]};
        int[] rightItem = new int[]{currentItem[0] + 1, currentItem[1]};
        int[] upperItem = new int[]{currentItem[0], currentItem[1] + 1};
        int[] lowerItem = new int[]{currentItem[0], currentItem[1] - 1};
        arrayList.add(leftItem);
        arrayList.add(rightItem);
        arrayList.add(upperItem);
        arrayList.add(lowerItem);
        return arrayList;
    }

    private static ArrayList<int[]> getEightNeighbourItem(int[] currentItem) {
        ArrayList<int[]> arrayList = getNeighbourItem(currentItem);
        int[] leftUpperItem = new int[]{currentItem[0] - 1, currentItem[1] + 1};
        int[] rightUpperItem = new int[]{currentItem[0] + 1, currentItem[1] + 1};
        int[] leftLowerItem = new int[]{currentItem[0] - 1, currentItem[1] - 1};
        int[] rightLowerItem = new int[]{currentItem[0] + 1, currentItem[1] - 1};
        arrayList.add(leftUpperItem);
        arrayList.add(rightUpperItem);
        arrayList.add(leftLowerItem);
        arrayList.add(rightLowerItem);
        return arrayList;
    }

    public int[] getInputCoordinate() {
        return this.arrayList.get(0);
    }

    public int[] getOutputCoordinate() {
        return this.arrayList.get(this.arrayList.size() - 1);
    }

    public void fillWithFloors(TETile[][] world) {
        for (int[] item : this.arrayList) {
            if (world[item[0]][item[1]] == Tileset.NOTHING) {
                world[item[0]][item[1]] = Tileset.FLOOR;
            }
        }
//        world[this.getInputCoordinate()[0]][this.getInputCoordinate()[1]] = Tileset.TREE;
        world[this.getInputCoordinate()[0]][this.getInputCoordinate()[1]] = Tileset.FLOOR;
    }

    public void fillWithWalls(TETile[][] world) {
        for (int[] item : arrayList.subList(0, arrayList.size() - 1)) {
            ArrayList<int[]> neighbourItems = HallWay.getEightNeighbourItem(item);
            for (int[] neighbour : neighbourItems) {
                if (world[neighbour[0]][neighbour[1]] == Tileset.NOTHING) {
                    world[neighbour[0]][neighbour[1]] = Tileset.WALL;
                }
            }
        }
    }
}

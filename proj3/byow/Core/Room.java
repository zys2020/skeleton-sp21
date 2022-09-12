package byow.Core;

import byow.TileEngine.TETile;

public interface Room {
    int[] getInputCoordinate();

    int[] getOutputCoordinate();

    void fillWithFloors(TETile[][] world);

    void fillWithWalls(TETile[][] world);

    boolean getStatus();

    String info();
}

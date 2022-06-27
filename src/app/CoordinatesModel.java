package app;

import java.io.Serializable;

public class CoordinatesModel implements Serializable {

    private int x;
    private int y;

    public CoordinatesModel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

package com.goli.marsrover.model;

public class Plateau {

    private final int maxX;
    private final int maxY;

    public Plateau(int maxX, int maxY) {
        if (maxX <= 0 || maxY <= 0)
            throw new IllegalArgumentException("Invalid plateau dimensions: " + maxX + ", " + maxY);
        this.maxX = maxX;
        this.maxY = maxY;
    }

    boolean isValidPosition(int x, int y) {
        return x >= 0 && x <= maxX && y >= 0 && y <= maxY;
    }
}

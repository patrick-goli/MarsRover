package com.goli.marsrover.model;


public enum Direction {

    NORTH('N'),
    EAST('E'),
    SOUTH('S'),
    WEST('W');

    private final char code;

    Direction(char code) {
        this.code = code;
    }

    public static Direction fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'N' -> NORTH;
            case 'E' -> EAST;
            case 'S' -> SOUTH;
            case 'W' -> WEST;
            default -> throw new IllegalArgumentException("Unknown direction: " + c);
        };
    }

    public char toChar() {
        return code;
    }

    public Direction rotateLeft() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public Direction rotateRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
}
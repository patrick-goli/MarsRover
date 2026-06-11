package com.goli.marsrover.model;


public enum Instruction {

    MOVE('M'),
    LEFT('L'),
    RIGHT('R');

    private final char code;

    Instruction(char code) {
        this.code = code;
    }

    public static Instruction fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'M' -> MOVE;
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            default -> throw new IllegalArgumentException("Unknown instruction: " + c);
        };
    }

    public char toChar() {
        return code;
    }
}
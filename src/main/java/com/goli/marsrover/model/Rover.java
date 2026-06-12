package com.goli.marsrover.model;


import com.goli.marsrover.exception.InvalidPositionException;


public class Rover {

    private final MissionController controller;
    private final Position position;

    public Rover(Position initialPosition, MissionController controller) {
        this.position = initialPosition;
        this.controller = controller;
    }

    public void move(Instruction instruction) throws InvalidPositionException {

        Coordinate destination = new Coordinate(position.getX(), position.getY());

        switch (instruction) {
            case LEFT -> rotateLeft();
            case RIGHT -> rotateRight();
            case MOVE -> destination = getDestinationAfterMove();
        }

        if (!controller.canMoveTo(this, destination.x, destination.y)) {
            throw new InvalidPositionException("Invalid move for rover: (" + destination.x + ", " + destination.y + ")");
        }
        controller.updatePosition(this, destination.x, destination.y);
        position.setX(destination.x);
        position.setY(destination.y);

    }

    private Coordinate getDestinationAfterMove() {
        int newX = position.getX();
        int newY = position.getY();

        switch (position.getDirection()) {
            case NORTH -> newY += 1;
            case SOUTH -> newY -= 1;
            case EAST -> newX += 1;
            case WEST -> newX -= 1;
        }
        return new Coordinate(newX, newY);
    }

    public void rotateLeft() {
        var newDirection = position.getDirection().rotateLeft();
        position.setDirection(newDirection);
    }

    public void rotateRight() {
        var newDirection = position.getDirection().rotateRight();
        position.setDirection(newDirection);
    }


    @Override
    public String toString() {
        return position.toString();
    }

    record Coordinate(int x, int y) {
    }
}

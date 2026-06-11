package com.goli.marsrover.model;


import com.goli.marsrover.exception.InvalidPositionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoverTest {

    @Test
    void move_forwardWithinBounds_updatesPosition() throws InvalidPositionException {
        Plateau plateau = new Plateau(5, 5);
        MissionController controller = new MissionController(plateau);

        Position initial = new Position(1, 1, Direction.NORTH);
        Rover rover = new Rover(initial, controller);
        controller.register(rover, initial);

        rover.move(Instruction.MOVE);

        assertEquals(1, initial.getX());
        assertEquals(2, initial.getY());
        assertEquals(Direction.NORTH, initial.getDirection());
    }

    @Test
    void rotateLeft_changesDirection() throws InvalidPositionException {
        Plateau plateau = new Plateau(5, 5);
        MissionController controller = new MissionController(plateau);

        Position initial = new Position(1, 1, Direction.NORTH);
        Rover rover = new Rover(initial, controller);
        controller.register(rover, initial);

        rover.move(Instruction.LEFT);

        assertEquals(Direction.WEST, initial.getDirection());
        assertEquals(1, initial.getX());
        assertEquals(1, initial.getY());
    }

    @Test
    void move_outOfBounds_throwsInvalidPositionException() {
        Plateau plateau = new Plateau(5, 5);
        MissionController controller = new MissionController(plateau);

        Position initial = new Position(5, 5, Direction.NORTH);
        Rover rover = new Rover(initial, controller);
        controller.register(rover, initial);

        assertThrows(InvalidPositionException.class,
                () -> rover.move(Instruction.MOVE));
    }

    @Test
    void move_intoOccupiedCell_throwsInvalidPositionException() {
        Plateau plateau = new Plateau(5, 5);
        MissionController controller = new MissionController(plateau);

        Position pos1 = new Position(1, 1, Direction.NORTH);
        Rover rover1 = new Rover(pos1, controller);
        controller.register(rover1, pos1);

        Position pos2 = new Position(1, 2, Direction.SOUTH);
        Rover rover2 = new Rover(pos2, controller);
        controller.register(rover2, pos2);

        // rover1 try to go to (1,2) occupied by rover2
        assertThrows(InvalidPositionException.class,
                () -> rover1.move(Instruction.MOVE));
    }
}
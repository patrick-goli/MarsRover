package com.goli.marsrover.model;

import com.goli.marsrover.exception.InvalidPositionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissionControllerTest {

    @Test
    void executeAll_runsRoversSequentiallyAndProducesExpectedPositions() {
        Plateau plateau = new Plateau(5, 5);
        MissionController controller = new MissionController(plateau);

        // Rover 1 : 1 2 N, LMLMLMLMM -> 1 3 N
        Position pos1 = new Position(1, 2, Direction.NORTH);
        Rover rover1 = new Rover(pos1, controller);
        controller.register(rover1, pos1);
        List<Instruction> instructions1 = "LMLMLMLMM".chars()
                .mapToObj(c -> (char) c)
                .map(Instruction::fromChar)
                .toList();
        RoverCommand cmd1 = new RoverCommand(rover1, instructions1);

        // Rover 2 : 3 3 E, MMRMMRMRRM -> 5 1 E
        Position pos2 = new Position(3, 3, Direction.EAST);
        Rover rover2 = new Rover(pos2, controller);
        controller.register(rover2, pos2);
        List<Instruction> instructions2 = "MMRMMRMRRM".chars()
                .mapToObj(c -> (char) c)
                .map(Instruction::fromChar)
                .toList();
        RoverCommand cmd2 = new RoverCommand(rover2, instructions2);

        try {
            for (RoverCommand cmd : List.of(cmd1, cmd2)) {
                for (Instruction instruction : cmd.instructions()) {
                    cmd.rover().move(instruction);
                }
            }
        } catch (InvalidPositionException e) {
            throw new RuntimeException("Unexpected error during mission", e);
        }

        assertEquals("1 3 N", rover1.toString());
        assertEquals("5 1 E", rover2.toString());
    }

    @Test
    void roverCannotMoveIntoOccupiedCell_andFinalPositionsRemainUnchanged() {
        Plateau plateau = new Plateau(5, 5);
        MissionController controller = new MissionController(plateau);

        // Rover 1 (1,1) face NORTH
        Position pos1 = new Position(1, 1, Direction.NORTH);
        Rover rover1 = new Rover(pos1, controller);
        controller.register(rover1, pos1);

        // Rover 2 (1,2) face SOUTH
        Position pos2 = new Position(1, 2, Direction.SOUTH);
        Rover rover2 = new Rover(pos2, controller);
        controller.register(rover2, pos2);

        // rover1 go to (1,2)
        InvalidPositionException ex = assertThrows(
                InvalidPositionException.class,
                () -> rover1.move(Instruction.MOVE)
        );
        assertTrue(ex.getMessage().contains("Move out of bounds"));

        // Check the positions remain the same
        assertEquals(1, pos1.getX());
        assertEquals(1, pos1.getY());
        assertEquals(Direction.NORTH, pos1.getDirection());

        assertEquals(1, pos2.getX());
        assertEquals(2, pos2.getY());
        assertEquals(Direction.SOUTH, pos2.getDirection());

        System.out.println("Final position rover1: " + rover1);
        System.out.println("Final position rover2: " + rover2);
    }
}
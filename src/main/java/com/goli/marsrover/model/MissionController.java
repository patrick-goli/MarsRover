package com.goli.marsrover.model;

import com.goli.marsrover.exception.InvalidPositionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionController {
    private static final Logger log = LoggerFactory.getLogger(MissionController.class);

    private final Plateau plateau;
    private final Map<Rover, Position> positions = new HashMap<>();

    public MissionController(Plateau plateau) {
        this.plateau = plateau;
    }

    public static void executeAll(List<RoverCommand> commands) {
        try {
            for (RoverCommand command : commands) {
                for (Instruction instruction : command.instructions()) {
                    command.rover().move(instruction);
                }
            }
        } catch (InvalidPositionException e) {
            // Stop everything if error
            log.error(e.getMessage());
            log.error("Stopping Mars Rovers");
        }
        // Print final position
        for (RoverCommand command : commands) {
            log.info(command.rover().toString());
        }
    }

    public void register(Rover rover, Position position) {
        positions.put(rover, position);
    }

    public boolean canMoveTo(Rover rover, int x, int y) {
        if (!plateau.isValidPosition(x, y))
            return false;
        // No collision with another rover
        return positions.entrySet().stream()
                .noneMatch(entry ->
                        entry.getKey() != rover &&
                                entry.getValue().getX() == x &&
                                entry.getValue().getY() == y
                );
    }

    public void updatePosition(Rover rover, int x, int y) {
        Position pos = positions.get(rover);
        if (pos != null) {
            pos.setX(x);
            pos.setY(y);
        }
    }

}

package com.goli.marsrover.input;

import com.goli.marsrover.exception.InvalidCommandException;
import com.goli.marsrover.model.*;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileInputSource implements InputSource {

    private final Path path;

    public FileInputSource(Path path) {
        this.path = path;
    }

    private static Plateau createPlateau(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Empty input");
        }

        // First line : plateau coordinates
        String[] plateauParts = line.strip().split("\\s+");
        if (plateauParts.length != 2) {
            throw new IllegalArgumentException("Invalid plateau line: " + line);
        }
        int maxX = Integer.parseInt(plateauParts[0]);
        int maxY = Integer.parseInt(plateauParts[1]);

        return new Plateau(maxX, maxY);
    }

    public List<RoverCommand> getRoverCommands() {
        List<RoverCommand> commands = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {

            String line = br.readLine();
            Plateau plateau = createPlateau(line);
            MissionController controller = new MissionController(plateau);

            //Read instructions
            while (true) {
                String positionLine = br.readLine();
                if (positionLine == null) {
                    break;
                }
                positionLine = positionLine.strip();
                if (positionLine.isEmpty()) {
                    continue;
                }

                String commandsLine = br.readLine();
                if (commandsLine == null) {
                    throw new IllegalArgumentException("Missing command line for rover");
                }
                commandsLine = commandsLine.strip();

                // Initial position of a rover
                String[] posParts = positionLine.split("\\s+");
                if (posParts.length != 3) {
                    throw new IllegalArgumentException("Invalid rover position line: " + positionLine);
                }
                int x = Integer.parseInt(posParts[0]);
                int y = Integer.parseInt(posParts[1]);
                Direction direction = Direction.fromChar(posParts[2].charAt(0));

                Position initialPos = new Position(x, y, direction);
                Rover rover = new Rover(initialPos, controller);
                controller.register(rover, initialPos);

                List<Instruction> instructions = commandsLine.chars()
                        .mapToObj(c -> (char) c)
                        .map(Instruction::fromChar)
                        .toList();

                commands.add(new RoverCommand(rover, instructions));
            }
        } catch (Exception e) {
            throw new InvalidCommandException(e);
        }
        return commands;
    }
}

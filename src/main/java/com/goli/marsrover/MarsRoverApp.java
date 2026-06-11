package com.goli.marsrover;

import com.goli.marsrover.input.FileInputSource;
import com.goli.marsrover.model.MissionController;
import com.goli.marsrover.model.RoverCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MarsRoverApp {
    private static final Logger log = LoggerFactory.getLogger(MarsRoverApp.class);


    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Invalid command line arguments");
            log.error("Usage: java -jar rover.jar <input-file>");
            System.exit(1);
        }
        Path path = Path.of(args[0]);
        if (Files.notExists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            log.error("Input file does not exist or is not readable: {}", args[0]);
            System.exit(1);
        }
        log.info("Mars Rover started");
        FileInputSource inputSource = new FileInputSource(path);
        List<RoverCommand> roverCommands = inputSource.getRoverCommands();

        MissionController.executeAll(roverCommands);

    }
}
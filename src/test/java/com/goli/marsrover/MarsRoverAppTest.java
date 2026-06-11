package com.goli.marsrover;


import com.goli.marsrover.input.FileInputSource;
import com.goli.marsrover.model.MissionController;
import com.goli.marsrover.model.RoverCommand;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MarsRoverAppTest {

    @Test
    void fullScenario_fromFileInput_producesExpectedFinalPositions() throws URISyntaxException {
        var url = getClass().getResource("/input.txt");
        assertNotNull(url, "input.txt not found on classpath");
        Path path = Path.of(url.toURI());

        FileInputSource source = new FileInputSource(path);
        List<RoverCommand> commands = source.getRoverCommands();

        MissionController.executeAll(commands);

        assertEquals("1 3 N", commands.get(0).rover().toString());
        assertEquals("5 1 E", commands.get(1).rover().toString());
    }
}

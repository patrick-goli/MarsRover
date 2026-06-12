package com.goli.marsrover.input;

import com.goli.marsrover.model.RoverCommand;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileInputSourceTest {

    @Test
    void getRoverCommands_parsesFileInputCorrectly() throws URISyntaxException {
        var url = getClass().getResource("/input.txt");
        assertNotNull(url, "input.txt not found on classpath");

        Path path = Path.of(url.toURI());
        FileInputSource source = new FileInputSource(path);

        List<RoverCommand> commands = source.getRoverCommands();
        assertEquals(2, commands.size());

        RoverCommand first = commands.getFirst();
        assertEquals("1 2 N", first.rover().toString());
        assertEquals(9, first.instructions().size());

        RoverCommand second = commands.get(1);
        assertEquals("3 3 E", second.rover().toString());
        assertEquals(10, second.instructions().size());
    }
}
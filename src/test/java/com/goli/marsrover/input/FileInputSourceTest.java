package com.goli.marsrover.input;

import com.goli.marsrover.exception.InvalidCommandException;
import com.goli.marsrover.model.RoverCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileInputSourceTest {

    @TempDir
    Path tempDir;

    @Test
    void getRoverCommands_parsesFileInputCorrectly() throws URISyntaxException {
        var url = getClass().getResource("/input.txt");
        assertNotNull(url, "input.txt not found on classpath");

        Path path = Path.of(url.toURI());
        FileInputSource source = new FileInputSource(path);

        List<RoverCommand> commands = source.getRoverCommands();

        assertEquals(2, commands.size());

        RoverCommand first = commands.get(0);
        assertEquals("1 2 N", first.rover().toString());
        assertEquals(9, first.instructions().size());

        RoverCommand second = commands.get(1);
        assertEquals("3 3 E", second.rover().toString());
        assertEquals(10, second.instructions().size());
    }

    @Test
    void getRoverCommands_failsOnBlankLines() throws IOException {
        Path file = tempDir.resolve("input-with-blanks.txt");
        Files.writeString(file, """
                
                  5   5
                
                  1   2   N
                  LMLMLMLMM
                
                  3 3 E
                  MMRMMRMRRM
                
                """);

        FileInputSource source = new FileInputSource(file);

        assertThrows(InvalidCommandException.class, source::getRoverCommands);

    }

    @Test
    void getRoverCommands_throwsWhenFileIsEmpty() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        FileInputSource source = new FileInputSource(file);

        assertThrows(InvalidCommandException.class, source::getRoverCommands);
    }

    @Test
    void getRoverCommands_throwsWhenPlateauLineIsInvalid() throws IOException {
        Path file = tempDir.resolve("invalid-plateau.txt");
        Files.writeString(file, """
                5
                1 2 N
                LMLMLMLMM
                """);

        FileInputSource source = new FileInputSource(file);

        InvalidCommandException ex =
                assertThrows(InvalidCommandException.class, source::getRoverCommands);

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Invalid plateau line"));
    }

    @Test
    void getRoverCommands_throwsWhenRoverPositionLineIsInvalid() throws IOException {
        Path file = tempDir.resolve("invalid-position.txt");
        Files.writeString(file, """
                5 5
                1 2
                LMLMLMLMM
                """);

        FileInputSource source = new FileInputSource(file);

        InvalidCommandException ex =
                assertThrows(InvalidCommandException.class, source::getRoverCommands);

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Invalid rover position line"));
    }

    @Test
    void getRoverCommands_throwsWhenCommandLineIsMissing() throws IOException {
        Path file = tempDir.resolve("missing-command-line.txt");
        Files.writeString(file, """
                5 5
                1 2 N
                """);

        FileInputSource source = new FileInputSource(file);

        InvalidCommandException ex =
                assertThrows(InvalidCommandException.class, source::getRoverCommands);

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Missing command line"));
    }

    @Test
    void getRoverCommands_throwsWhenDirectionIsInvalid() throws IOException {
        Path file = tempDir.resolve("invalid-direction.txt");
        Files.writeString(file, """
                5 5
                1 2 X
                LMLMLMLMM
                """);

        FileInputSource source = new FileInputSource(file);

        assertThrows(InvalidCommandException.class, source::getRoverCommands);
    }

    @Test
    void getRoverCommands_throwsWhenInstructionIsInvalid() throws IOException {
        Path file = tempDir.resolve("invalid-instruction.txt");
        Files.writeString(file, """
                5 5
                1 2 N
                LMXMLMLMM
                """);

        FileInputSource source = new FileInputSource(file);

        assertThrows(InvalidCommandException.class, source::getRoverCommands);
    }
}
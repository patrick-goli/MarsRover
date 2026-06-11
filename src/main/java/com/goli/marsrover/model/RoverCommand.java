package com.goli.marsrover.model;

import java.util.List;

public record RoverCommand(Rover rover, List<Instruction> instructions) {
}

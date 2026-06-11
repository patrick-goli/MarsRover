package com.goli.marsrover.input;

import com.goli.marsrover.model.RoverCommand;

import java.util.List;

public interface InputSource {

    List<RoverCommand> getRoverCommands();
}

package com.goli.marsrover.exception;

public class InvalidCommandException extends RuntimeException {

    public InvalidCommandException(Exception e) {
        super(e);
    }
}

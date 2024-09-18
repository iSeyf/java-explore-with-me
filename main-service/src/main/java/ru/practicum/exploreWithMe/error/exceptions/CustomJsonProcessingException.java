package ru.practicum.exploreWithMe.error.exceptions;

public class CustomJsonProcessingException extends RuntimeException {
    public CustomJsonProcessingException(String message) {
        super(message);
    }
}
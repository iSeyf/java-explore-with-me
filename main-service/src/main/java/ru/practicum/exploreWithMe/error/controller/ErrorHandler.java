package ru.practicum.exploreWithMe.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exploreWithMe.error.exceptions.BadRequestException;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.error.exceptions.CustomJsonProcessingException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.error.model.ApiError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()),
                e.getMessage(),
                "Bad request",
                HttpStatus.BAD_REQUEST.toString(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()),
                e.getMessage(),
                "Resource not found",
                HttpStatus.NOT_FOUND.toString(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()),
                e.getMessage(),
                "Data conflict",
                HttpStatus.CONFLICT.toString(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCustomJsonProcessingException(final CustomJsonProcessingException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()),
                e.getMessage(),
                "JSON Processing Error",
                HttpStatus.BAD_REQUEST.toString(),
                LocalDateTime.now());
    }
}

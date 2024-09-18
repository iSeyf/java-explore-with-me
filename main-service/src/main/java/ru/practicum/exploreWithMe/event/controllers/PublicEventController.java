package ru.practicum.exploreWithMe.event.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public ResponseEntity<Object> searchEventsPublic(@RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false) String rangeStart,
                                                     @RequestParam(required = false) String rangeEnd,
                                                     @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                     @RequestParam(required = false) String sort,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     HttpServletRequest request) {
        return ResponseEntity.status(200).body(service.searchEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> getEventByIdPublic(@PathVariable long eventId, HttpServletRequest request) {
        return ResponseEntity.status(200).body(service.getEventByIdPublic(eventId, request));
    }
}

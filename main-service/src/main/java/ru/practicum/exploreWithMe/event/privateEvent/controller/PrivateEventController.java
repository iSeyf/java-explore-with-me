package ru.practicum.exploreWithMe.event.privateEvent.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventUserRequest;
import ru.practicum.exploreWithMe.event.privateEvent.service.PrivateEventService;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService service;

    @GetMapping
    public ResponseEntity<Object> getUserEvents(@PathVariable long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(service.getUserEvents(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<Object> addNewEvent(@PathVariable long userId,
                                              @Valid @RequestBody NewEventDto newEventDto) {
        return ResponseEntity.status(201).body(service.addNewEvent(userId, newEventDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> getEventById(@PathVariable long userId,
                                               @PathVariable long eventId) {
        return ResponseEntity.status(200).body(service.getEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable long userId,
                                              @PathVariable long eventId,
                                              @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return ResponseEntity.status(200).body(service.updateEvent(userId, eventId, updateEventUserRequest));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<Object> getEventRequests(@PathVariable long userId,
                                                   @PathVariable long eventId) {
        return ResponseEntity.status(200).body(service.getEventRequests(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<Object> updateRequestsStatus(@PathVariable long userId,
                                                       @PathVariable long eventId,
                                                       @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return ResponseEntity.status(200).body(service.updateRequestsStatus(userId, eventId, request));
    }
}

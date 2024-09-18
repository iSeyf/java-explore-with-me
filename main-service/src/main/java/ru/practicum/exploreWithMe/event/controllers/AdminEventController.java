package ru.practicum.exploreWithMe.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.event.service.EventService;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public ResponseEntity<Object> searchEvents(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<String> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(service.searchEventAdmin(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEventAdmin(@PathVariable long eventId,
                                                   @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return ResponseEntity.status(200).body(service.updateEventAdmin(eventId, updateEventAdminRequest));
    }
}

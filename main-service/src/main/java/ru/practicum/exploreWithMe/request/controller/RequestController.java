package ru.practicum.exploreWithMe.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.request.service.RequestService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getRequests(@PathVariable long userId) {
        return ResponseEntity.status(200).body(service.getRequests(userId));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> createRequest(@PathVariable long userId, @RequestParam long eventId) {
        return ResponseEntity.status(201).body(service.createRequest(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        return ResponseEntity.status(200).body(service.cancelRequest(userId, requestId));
    }
}

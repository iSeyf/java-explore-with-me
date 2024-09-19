package ru.practicum.exploreWithMe.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.subscription.service.SubscriptionService;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService service;

    @PostMapping("/subscription")
    public ResponseEntity<Object> subscribeToUser(@PathVariable long userId,
                                                  @RequestParam long subscribedToId) {
        return ResponseEntity.status(201).body(service.subscribeToUser(userId, subscribedToId));
    }

    @DeleteMapping("/subscription")
    public ResponseEntity<Object> unsubscribe(@PathVariable long userId,
                                              @RequestParam long subscribedToId) {
        service.unsubscribe(userId, subscribedToId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/subscribers")
    public ResponseEntity<Object> getUserSubscribers(@PathVariable long userId) {
        return ResponseEntity.status(200).body(service.getUserSubscribers(userId));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<Object> getUserSubscriptions(@PathVariable long userId) {
        return ResponseEntity.status(200).body(service.getUserSubscriptions(userId));
    }

    @GetMapping("/subscriptions/feed")
    public ResponseEntity<Object> getFeed(@PathVariable long userId,
                                          @RequestParam(required = false, defaultValue = "0") int from,
                                          @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(service.getFeed(userId, from, size));
    }
}

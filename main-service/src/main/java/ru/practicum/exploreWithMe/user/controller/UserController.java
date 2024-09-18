package ru.practicum.exploreWithMe.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.user.dto.NewUserDto;
import ru.practicum.exploreWithMe.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(service.getUsers(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody NewUserDto newUserDto) {
        return ResponseEntity.status(201).body(service.createUser(newUserDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }
}

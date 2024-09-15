package ru.practicum.exploreWithMe.compilation.publicCompilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.compilation.publicCompilation.service.PublicCompilationService;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final PublicCompilationService service;

    @GetMapping
    public ResponseEntity<Object> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(service.getCompilations(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<Object> getCompilationById(@PathVariable long compId) {
        return ResponseEntity.status(200).body(service.getCompilationById(compId));
    }
}

package ru.practicum.exploreWithMe.compilation.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.compilation.service.CompilationService;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService service;

    @PostMapping
    public ResponseEntity<Object> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(201).body(service.createCompilation(newCompilationDto));
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<Object> updateCompilation(@PathVariable long compId,
                                                    @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        return ResponseEntity.status(200).body(service.updateCompilation(compId, updateCompilationDto));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable long compId) {
        service.deleteCompilation(compId);
        return ResponseEntity.status(204).build();
    }
}

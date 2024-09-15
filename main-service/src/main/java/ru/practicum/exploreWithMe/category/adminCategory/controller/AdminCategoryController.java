package ru.practicum.exploreWithMe.category.adminCategory.controller;

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
import ru.practicum.exploreWithMe.category.adminCategory.service.AdminCategoryService;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService service;

    @PostMapping
    public ResponseEntity<Object> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return ResponseEntity.status(201).body(service.createCategory(newCategoryDto));
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> updateCategory(@PathVariable long catId, @Valid @RequestBody NewCategoryDto newCategoryDto) {
        return ResponseEntity.status(200).body(service.updateCategory(catId, newCategoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable long catId) {
        service.deleteCategory(catId);
        return ResponseEntity.status(204).build();
    }
}

package ru.practicum.exploreWithMe.category.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exploreWithMe.category.service.CategoryService;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<Object> getCategoriesPublic(@RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(service.getCategories(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getCategoryByIdPublic(@PathVariable long catId) {
        return ResponseEntity.status(200).body(service.getCategoryById(catId));
    }
}

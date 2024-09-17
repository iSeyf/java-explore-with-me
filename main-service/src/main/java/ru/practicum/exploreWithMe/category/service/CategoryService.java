package ru.practicum.exploreWithMe.category.service;

import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.dto.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategoryAdmin(NewCategoryDto newCategoryDto);

    CategoryDto updateCategoryAdmin(long catId, UpdateCategoryDto updateCategoryDto);

    void deleteCategoryAdmin(long catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);
}

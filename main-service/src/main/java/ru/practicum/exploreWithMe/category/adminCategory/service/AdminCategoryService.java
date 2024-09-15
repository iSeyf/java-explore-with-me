package ru.practicum.exploreWithMe.category.adminCategory.service;

import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);
}

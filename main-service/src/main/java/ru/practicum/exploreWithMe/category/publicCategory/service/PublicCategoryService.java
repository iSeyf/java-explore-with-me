package ru.practicum.exploreWithMe.category.publicCategory.service;

import ru.practicum.exploreWithMe.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);
}

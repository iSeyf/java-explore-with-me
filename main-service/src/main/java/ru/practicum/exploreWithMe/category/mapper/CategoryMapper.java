package ru.practicum.exploreWithMe.category.mapper;

import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categories) {
            categoryDtoList.add(toCategoryDto(category));
        }
        return categoryDtoList;
    }
}

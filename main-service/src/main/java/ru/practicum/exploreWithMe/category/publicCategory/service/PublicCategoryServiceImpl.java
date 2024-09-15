package ru.practicum.exploreWithMe.category.publicCategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository repository;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return CategoryMapper.toCategoryDtoList(repository.findAll(PageRequest.of(from, size)).toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        return CategoryMapper.toCategoryDto(repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена.")));
    }
}

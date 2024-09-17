package ru.practicum.exploreWithMe.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.dto.UpdateCategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategoryAdmin(NewCategoryDto newCategoryDto) {
        List<Category> categoryList = categoryRepository.findAll();
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с таким названием уже существует.");
        }

        Category savedCategory = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategoryAdmin(long catId, UpdateCategoryDto updateCategoryDto) {
        Category oldCategory = categoryRepository.findCategoryById(catId);

        if (!oldCategory.getName().equalsIgnoreCase(updateCategoryDto.getName())) {
            if (categoryRepository.existsByName(updateCategoryDto.getName())) {
                throw new ConflictException("Категория с таким названием уже существует.");
            }
        }

        oldCategory.setName(updateCategoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(oldCategory));
    }

    @Override
    public void deleteCategoryAdmin(long catId) {
        categoryRepository.findCategoryById(catId);
        List<Event> eventList = eventRepository.findByCategoryId(catId);
        if (!eventList.isEmpty()) {
            throw new ConflictException("Невозможно удалить категорию, так как она связана с событиями.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return CategoryMapper.toCategoryDtoList(categoryRepository.findAll(PageRequest.of(from, size)).toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findCategoryById(catId));
    }
}

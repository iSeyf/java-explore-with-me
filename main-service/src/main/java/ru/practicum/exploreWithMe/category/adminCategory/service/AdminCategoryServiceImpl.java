package ru.practicum.exploreWithMe.category.adminCategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        List<Category> categoryList = categoryRepository.findAll();
        for (Category category : categoryList) {
            if (category.getName().equals(newCategoryDto.getName())) {
                throw new ConflictException("Категория с таким названием уже существует.");
            }
        }
        Category savedCategory = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto) {
        Category oldCategory = checkCategory(catId);
        List<Category> categoryList = categoryRepository.findAll();
        for (Category category : categoryList) {
            if (category.getId() != catId) {
                if (category.getName().equals(newCategoryDto.getName())) {
                    throw new ConflictException("Категория с таким названием уже существует.");
                }
            }
        }
        oldCategory.setName(newCategoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(oldCategory));
    }

    @Override
    public void deleteCategory(long catId) {
        checkCategory(catId);
        List<Event> eventList = eventRepository.findByCategoryId(catId);
        if (!eventList.isEmpty()) {
            throw new ConflictException("Невозможно удалить категорию, так как она связана с событиями.");
        }
        categoryRepository.deleteById(catId);
    }

    private Category checkCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена."));
    }
}

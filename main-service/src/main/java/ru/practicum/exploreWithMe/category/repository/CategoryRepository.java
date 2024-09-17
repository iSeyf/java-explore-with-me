package ru.practicum.exploreWithMe.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    default Category findCategoryById(long catId) {
        return findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена."));
    }
}

package ru.practicum.exploreWithMe.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Category c WHERE c.name = :name AND c.id <> :id")
    boolean existsByNameAndNotId(@Param("name") String name, @Param("id") long id);


    default Category findCategoryById(long catId) {
        return findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена."));
    }
}

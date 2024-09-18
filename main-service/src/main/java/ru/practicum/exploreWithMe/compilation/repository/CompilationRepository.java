package ru.practicum.exploreWithMe.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c WHERE (:pinned IS NULL OR c.pinned = :pinned)")
    Page<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);

    default Compilation findCompilationById(long compilationId) {
        return findById(compilationId).orElseThrow(() -> new NotFoundException("Подборка не найдена."));
    }
}

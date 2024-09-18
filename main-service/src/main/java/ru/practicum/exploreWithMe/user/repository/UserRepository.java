package ru.practicum.exploreWithMe.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE (:ids IS NULL OR u.id IN :ids)")
    Page<User> findByIdIn(@Param("ids") List<Long> ids, Pageable pageable);

    default User findUserById(long userId) {
        return findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }
}

package ru.practicum.exploreWithMe.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.initiator.id = :userId")
    Page<Event> findByInitiatorId(long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE "
            + "(:users IS NULL OR e.initiator.id IN :users) AND "
            + "(:states IS NULL OR e.state IN :states) AND "
            + "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(e.eventDate >= :rangeStart AND e.eventDate <= :rangeEnd) ")
    Page<Event> searchEvents(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("SELECT e FROM Event e "
            + "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) "
            + "   OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) "
            + "AND (:categories IS NULL OR e.category.id IN :categories) "
            + "AND (:paid IS NULL OR e.paid = :paid) "
            + "AND (e.eventDate >= :rangeStart AND e.eventDate <= :rangeEnd) "
            + "AND e.state = 'PUBLISHED' "
            + "AND (e.participantLimit > e.confirmedRequests) ")
    Page<Event> findAllByPublicFiltersAndOnlyAvailable(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (COALESCE(:text, '') = '' OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) "
            + "AND (:categories IS NULL OR e.category.id IN :categories) "
            + "AND (:paid IS NULL OR e.paid = :paid) "
            + "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd "
            + "AND e.state = 'PUBLISHED' ")
    Page<Event> findAllByPublicFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("SELECT e FROM Event e WHERE e.category.id = :categoryId")
    List<Event> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT e FROM Event e WHERE e.initiator.id IN :subscribedUsers AND e.state = 'PUBLISHED' ORDER BY e.publishedOn DESC")
    Page<Event> getFeed(@Param("subscribedUsers") List<Long> subscribedUsers, Pageable pageable);

    default Event findEventById(long eventId) {
        return findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено."));
    }
}

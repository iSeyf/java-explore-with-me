package ru.practicum.exploreWithMe.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(long requesterId);

    List<Request> findAllByEventId(long eventId);

    boolean existsByRequesterIdAndEventId(long requesterId, long eventId);

    Request findByRequesterIdAndId(long requesterId, long requestId);

    default Request findRequestById(long requestId) {
        return findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден."));
    }
}

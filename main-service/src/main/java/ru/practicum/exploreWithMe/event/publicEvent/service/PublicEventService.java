package ru.practicum.exploreWithMe.event.publicEvent.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;

import java.util.List;

public interface PublicEventService {
    List<EventShortDto> searchEventsPublic(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           String rangeStart,
                                           String rangeEnd,
                                           boolean onlyAvailable,
                                           String sort,
                                           int from,
                                           int size,
                                           HttpServletRequest request);

    EventFullDto getEventByIdPublic(long id, HttpServletRequest request);
}

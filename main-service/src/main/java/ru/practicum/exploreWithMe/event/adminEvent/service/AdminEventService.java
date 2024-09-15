package ru.practicum.exploreWithMe.event.adminEvent.service;

import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> searchEvent(List<Long> users,
                                   List<String> states,
                                   List<Long> categories,
                                   String rangeStart,
                                   String rangeEnd,
                                   int from,
                                   int size);

    EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}

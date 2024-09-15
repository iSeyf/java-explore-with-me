package ru.practicum.exploreWithMe.event.privateEvent.service;

import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventUserRequest;
import ru.practicum.exploreWithMe.request.dto.RequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getUserEvents(long userId, int from, int size);

    EventFullDto addNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventById(long userId, long eventId);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<RequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

}

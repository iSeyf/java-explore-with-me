package ru.practicum.exploreWithMe.event.adminEvent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;
import ru.practicum.exploreWithMe.event.mappers.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.State;
import ru.practicum.exploreWithMe.event.model.StateAction;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public List<EventFullDto> searchEvent(List<Long> users,
                                          List<String> states,
                                          List<Long> categories,
                                          String rangeStart,
                                          String rangeEnd,
                                          int from,
                                          int size) {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusYears(1000);

        if (rangeStart != null && !rangeStart.isEmpty()) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        users = (users != null && !users.isEmpty()) ? users : null;
        states = (states != null && !states.isEmpty()) ? states : null;
        categories = (categories != null && !categories.isEmpty()) ? categories : null;

        Page<Event> eventsPage = eventRepository.searchEvents(users, states, categories, start, end, PageRequest.of(from, size));

        return EventMapper.toEventFullDtoList(eventsPage.getContent());
    }

    @Override
    public EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена."));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLat(updateEventAdminRequest.getLocation().getLat());
            event.setLon(updateEventAdminRequest.getLocation().getLon());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (event.getState().equals(State.PENDING)) {
                if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    event.setState(State.CANCELED);
                }
            } else if (event.getState().equals(State.PUBLISHED)) {
                if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    throw new ConflictException("Событие уже опубликовано и не может быть опубликовано повторно.");
                } else {
                    throw new ConflictException("Событие уже опубликовано и не может быть отменено.");
                }
            } else {
                if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    throw new ConflictException("Событие отменено и не может быть опубликовано.");
                } else {
                    throw new ConflictException("Событие уже отменено и не может быть отменено повторно.");
                }
            }
        }
        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(updatedEvent);
    }
}

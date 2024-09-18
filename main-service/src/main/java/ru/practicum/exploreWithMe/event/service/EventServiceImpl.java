package ru.practicum.exploreWithMe.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.client.StatsClient;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.error.exceptions.BadRequestException;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.error.exceptions.CustomJsonProcessingException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;
import ru.practicum.exploreWithMe.event.dto.UpdateEventUserRequest;
import ru.practicum.exploreWithMe.event.mappers.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventRequestStatus;
import ru.practicum.exploreWithMe.event.model.EventSort;
import ru.practicum.exploreWithMe.event.model.State;
import ru.practicum.exploreWithMe.event.model.StateAction;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.mapper.RequestMapper;
import ru.practicum.exploreWithMe.request.model.Request;
import ru.practicum.exploreWithMe.request.model.Status;
import ru.practicum.exploreWithMe.request.repository.RequestRepository;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> searchEventAdmin(List<Long> users,
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
        Event event = eventRepository.findEventById(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(updateEventAdminRequest.getCategory());
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

    @Override
    public List<EventShortDto> getUserEventsPrivate(long userId, int from, int size) {
        userRepository.findUserById(userId);
        Page<Event> page = eventRepository.findByInitiatorId(userId, PageRequest.of(from, size));

        return EventMapper.toEventShortDtoList(page.toList());
    }

    @Override
    public EventFullDto addNewEventPrivate(long userId, NewEventDto newEventDto) {
        User user = userRepository.findUserById(userId);
        Category category = categoryRepository.findCategoryById(newEventDto.getCategory());
        Event newEvent = EventMapper.toEvent(newEventDto, category, user);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(State.PENDING);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(newEvent));
        eventFullDto.setViews(0);

        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByIdPrivate(long userId, long eventId) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException("Вы не являетесь инициатором данного события.");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя обновить опубликованное событие");
        }
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Вы не являетесь инициатором данного события");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(updateEventUserRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLat(updateEventUserRequest.getLocation().getLat());
            event.setLon(updateEventUserRequest.getLocation().getLon());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new BadRequestException("Некорректное значение.");
            }
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(0);
        return eventFullDto;
    }

    @Override
    public List<RequestDto> getEventRequestsPrivate(long userId, long eventId) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Вы не являетесь инициатором этого события.");
        }
        return RequestMapper.toRequestDtoList(requestRepository.findAllByEventId(eventId));

    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatusPrivate(long userId, long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("У вас нет прав на изменение этого события");
        }

        if (!event.getRequestModeration()) {
            throw new BadRequestException("Модерация запросов не требуется для этого события");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Уже достигнут лимит участников для этого события");
        }

        List<Request> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());
        if (requests.size() != statusUpdateRequest.getRequestIds().size()) {
            throw new NotFoundException("Один или несколько запросов не найдены");
        }

        EventRequestStatusUpdateResult resultDto = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        for (Request request : requests) {
            if (statusUpdateRequest.getStatus().equals(EventRequestStatus.REJECTED)) {
                if (request.getStatus().equals(Status.CONFIRMED)) {
                    throw new ConflictException("Запрос уже подтвержден и не может быть изменен.");
                }
                request.setStatus(Status.REJECTED);
                resultDto.getRejectedRequests().add(RequestMapper.toRequestDto(request));
            } else if (statusUpdateRequest.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(Status.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    resultDto.getConfirmedRequests().add(RequestMapper.toRequestDto(request));
                } else {
                    request.setStatus(Status.REJECTED);
                    resultDto.getRejectedRequests().add(RequestMapper.toRequestDto(request));
                }
            }
        }

        requestRepository.saveAll(requests);
        eventRepository.save(event);

        return resultDto;
    }

    @Override
    public List<EventShortDto> searchEventsPublic(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size,
                                                  HttpServletRequest request) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusYears(1000);
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (end.isBefore(start)) {
            throw new BadRequestException("Дата окончания поиска не может быть раньше даты начала.");
        }
        EventSort sortValue;
        if (sort != null) {
            if ("EVENT_DATE".equalsIgnoreCase(sort)) {
                sortValue = EventSort.EVENT_DATE;
            } else if ("VIEWS".equalsIgnoreCase(sort)) {
                sortValue = EventSort.VIEWS;
            } else {
                throw new BadRequestException("Некорректное значение для сортировки");
            }
        } else {
            sortValue = EventSort.VIEWS;
        }


        Page<Event> page;
        if (onlyAvailable) {
            page = eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, PageRequest.of(from, size));
        } else {
            page = eventRepository.findAllByPublicFilters(text, categories, paid, start, end, PageRequest.of(from, size));
        }

        List<Event> events = page.getContent();
        List<String> eventUris = new ArrayList<>();
        for (Event event : events) {
            eventUris.add(request.getRequestURI() + "/" + event.getId());
        }

        List<Event> sortEventList = new ArrayList<>(events);
        sortEventList.sort(Comparator.comparing(Event::getPublishedOn));

        List<ViewStatsDto> viewStats = getViewStats(start, end, eventUris);

        Map<String, Long> viewsMap = new HashMap<>();
        for (ViewStatsDto stat : viewStats) {
            viewsMap.put(stat.getUri(), stat.getHits());
        }

        addHit(request);

        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            EventShortDto dto = EventMapper.toEventShortDto(event);
            String eventUri = request.getRequestURI() + "/" + event.getId();
            dto.setViews(viewsMap.getOrDefault(eventUri, 0L));
            eventShortDtos.add(dto);
        }

        if (sortValue.equals(EventSort.EVENT_DATE)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
        }


        return eventShortDtos;
    }

    @Override
    public EventFullDto getEventByIdPublic(long id, HttpServletRequest request) {
        Event event = eventRepository.findEventById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + id + " не опубликовано.");
        }
        addHit(request);

        List<ViewStatsDto> viewStats = getViewStats(event.getPublishedOn().minusSeconds(1), LocalDateTime.now(), List.of(request.getRequestURI()));

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        if (!viewStats.isEmpty()) {
            eventFullDto.setViews(viewStats.get(0).getHits());
        } else {
            eventFullDto.setViews(0);
        }

        return eventFullDto;
    }

    private void addHit(HttpServletRequest request) {
        statsClient.addHit(new EndpointHitDto(
                "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }

    private List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        ResponseEntity<Object> uriViewStats = statsClient.getStats(start, end, uris, true);
        List<ViewStatsDto> viewStats = new ArrayList<>();
        if (uriViewStats.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(uriViewStats.getBody());
                viewStats = objectMapper.readValue(json, new TypeReference<List<ViewStatsDto>>() {
                });
            } catch (JsonProcessingException e) {
                throw new CustomJsonProcessingException("Error processing JSON");
            }
        }
        return viewStats;
    }
}

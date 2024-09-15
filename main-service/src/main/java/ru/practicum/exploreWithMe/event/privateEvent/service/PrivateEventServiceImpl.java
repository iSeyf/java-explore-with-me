package ru.practicum.exploreWithMe.event.privateEvent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.error.exceptions.BadRequestException;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.exploreWithMe.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventUserRequest;
import ru.practicum.exploreWithMe.event.mappers.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventRequestStatus;
import ru.practicum.exploreWithMe.event.model.State;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.mapper.RequestMapper;
import ru.practicum.exploreWithMe.request.model.Request;
import ru.practicum.exploreWithMe.request.model.Status;
import ru.practicum.exploreWithMe.request.repository.RequestRepository;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        findUser(userId);
        Page<Event> page = eventRepository.findByInitiatorId(userId, PageRequest.of(from, size));

        return EventMapper.toEventShortDtoList(page.toList());
    }

    @Override
    public EventFullDto addNewEvent(long userId, NewEventDto newEventDto) {
        User user = findUser(userId);
        Category category = findCategory(newEventDto.getCategory());
        Event newEvent = EventMapper.toEvent(newEventDto, category, user);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(State.PENDING);
        Event event = eventRepository.save(newEvent);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventById(long userId, long eventId) {
        findUser(userId);
        Event event = findEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException("Вы не являетесь инициатором данного события.");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        findUser(userId);
        Event event = findEvent(eventId);
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
            Category category = findCategory(updateEventUserRequest.getCategory());
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
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<RequestDto> getEventRequests(long userId, long eventId) {
        findUser(userId);
        Event event = findEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Вы не являетесь инициатором этого события.");
        }
        return RequestMapper.toRequestDtoList(requestRepository.findAllByEventId(eventId));

    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        findUser(userId);
        Event event = findEvent(eventId);

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

    private User findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено."));
    }

    private Category findCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена."));
    }
}

package ru.practicum.exploreWithMe.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.event.model.Event;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        User user = userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Запросы можно создавать только на опубликованные события.");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Запрос от этого пользователя на данное событие уже существует.");
        }

        if (event.getInitiator() == user) {
            throw new ConflictException("Нельзя создать запрос на собственное событие.");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников для данного события.");
        }

        Request request = new Request();

        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            if (event.getRequestModeration()) {
                request.setStatus(Status.PENDING);
            } else {
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                eventRepository.save(event);
            }
        }
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        Request savedRequest = requestRepository.save(request);
        return RequestMapper.toRequestDto(savedRequest);
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId);

        request.setStatus(Status.CANCELED);
        Request canceledRequest = requestRepository.save(request);
        return RequestMapper.toRequestDto(canceledRequest);
    }

    @Override
    public List<RequestDto> getRequests(long userId) {
        userRepository.findUserById(userId);
        return RequestMapper.toRequestDtoList(requestRepository.findAllByRequesterId(userId));
    }
}

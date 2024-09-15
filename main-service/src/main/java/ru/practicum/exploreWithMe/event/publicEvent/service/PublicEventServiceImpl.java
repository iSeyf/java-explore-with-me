package ru.practicum.exploreWithMe.event.publicEvent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.client.StatsClient;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.error.exceptions.BadRequestException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.mappers.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventSort;
import ru.practicum.exploreWithMe.event.model.State;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository repository;
    private final StatsClient statsClient;

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
            sortValue = EventSort.EVENT_DATE;
        }


        Page<Event> page;
        if (onlyAvailable) {
            page = repository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, sortValue.toString(), PageRequest.of(from, size));
        } else {
            page = repository.findAllByPublicFilters(text, categories, paid, start, end, sortValue.toString(), PageRequest.of(from, size));
        }
        return EventMapper.toEventShortDtoList(page.getContent());
    }

    @Override
    public EventFullDto getEventByIdPublic(long id, HttpServletRequest request) {
        Event event = repository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + id + " не опубликовано.");
        }
        statsClient.addHit(new EndpointHitDto("main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        ResponseEntity<Object> uriViewStats = statsClient.getStats(
                LocalDateTime.now().minusYears(1000),
                LocalDateTime.now().plusYears(1000),
                List.of(request.getRequestURI()),
                true
        );
        List<ViewStatsDto> viewStats = new ArrayList<>();
        if (uriViewStats.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(uriViewStats.getBody());
                viewStats = objectMapper.readValue(json, new TypeReference<List<ViewStatsDto>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка при обработке JSON", e);
            }
        }
        event.setViews(viewStats.get(0).getHits());
        return EventMapper.toEventFullDto(repository.save(event));
    }
}

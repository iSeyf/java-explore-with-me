package ru.practicum.exploreWithMe.stats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.exploreWithMe.client.StatsClient;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.error.exceptions.CustomJsonProcessingException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsService {
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        ResponseEntity<Object> uriViewStats = statsClient.getStats(start, end, uris, true);
        List<ViewStatsDto> viewStats = new ArrayList<>();
        if (uriViewStats.getBody() != null) {
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

    public void addHit(HttpServletRequest request) {
        statsClient.addHit(new EndpointHitDto(
                "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }
}

package ru.practicum.exploreWithMe.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.event.model.State;
import ru.practicum.exploreWithMe.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    private long participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private long views;
}

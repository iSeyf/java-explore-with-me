package ru.practicum.exploreWithMe.request.mapper;

import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.model.Request;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }

    public static List<RequestDto> toRequestDtoList(List<Request> requests) {
        List<RequestDto> requestDtoList = new ArrayList<>();
        for (Request request : requests) {
            requestDtoList.add(toRequestDto(request));
        }
        return requestDtoList;
    }
}

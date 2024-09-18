package ru.practicum.exploreWithMe.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.request.model.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private Status status;
}

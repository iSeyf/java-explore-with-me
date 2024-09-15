package ru.practicum.exploreWithMe.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    private Boolean pinned;
    private List<Long> events;
    @Size(min = 1, max = 50)
    private String title;
}

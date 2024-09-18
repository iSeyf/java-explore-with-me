package ru.practicum.exploreWithMe.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private boolean pinned;
    private List<Long> events;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}

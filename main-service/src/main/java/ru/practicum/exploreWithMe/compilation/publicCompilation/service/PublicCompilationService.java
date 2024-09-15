package ru.practicum.exploreWithMe.compilation.publicCompilation.service;

import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long comId);
}

package ru.practicum.exploreWithMe.compilation.service;

import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(long compId, UpdateCompilationDto updateCompilationDto);

    void deleteCompilation(long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long comId);
}

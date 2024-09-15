package ru.practicum.exploreWithMe.compilation.adminCompilation.service;

import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;

public interface AdminCompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(long compId, UpdateCompilationDto updateCompilationDto);

    void deleteCompilation(long compId);
}

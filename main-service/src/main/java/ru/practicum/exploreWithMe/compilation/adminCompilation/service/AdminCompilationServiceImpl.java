package ru.practicum.exploreWithMe.compilation.adminCompilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;
import ru.practicum.exploreWithMe.compilation.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.compilation.repository.CompilationRepository;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Compilation newCompilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));

        return CompilationMapper.toCompilationDto(newCompilation);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation oldCompilation = checkCompilation(compId);
        if (updateCompilationDto.getPinned() != null) {
            oldCompilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            oldCompilation.setTitle(updateCompilationDto.getTitle());
        }
        if (updateCompilationDto.getEvents() != null && !updateCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(updateCompilationDto.getEvents());
            oldCompilation.setEvents(events);
        }
        Compilation updatedCompilation = compilationRepository.save(oldCompilation);
        return CompilationMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    private Compilation checkCompilation(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена"));
    }
}

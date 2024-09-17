package ru.practicum.exploreWithMe.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;
import ru.practicum.exploreWithMe.compilation.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.compilation.repository.CompilationRepository;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

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
        Compilation oldCompilation = compilationRepository.findCompilationById(compId);
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
        compilationRepository.findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Page<Compilation> page = compilationRepository.findByPinned(pinned, PageRequest.of(from, size));
        return CompilationMapper.toCompilationDtoList(page.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findCompilationById(compId));
    }
}

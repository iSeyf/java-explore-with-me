package ru.practicum.exploreWithMe.compilation.mapper;

import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.event.mappers.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto compilationDto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationDto.isPinned());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setEvents(events);
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(EventMapper.toEventShortDtoList(compilation.getEvents()));
        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilationList) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilationList) {
            compilationDtoList.add(toCompilationDto(compilation));
        }
        return compilationDtoList;
    }

}

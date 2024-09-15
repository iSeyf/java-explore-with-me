package ru.practicum.exploreWithMe.user.service;

import ru.practicum.exploreWithMe.user.dto.NewUserDto;
import ru.practicum.exploreWithMe.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserDto newUserDto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long userId);
}

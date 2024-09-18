package ru.practicum.exploreWithMe.user.mapper;

import ru.practicum.exploreWithMe.user.dto.NewUserDto;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.dto.UserShortDto;
import ru.practicum.exploreWithMe.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static User toUser(NewUserDto newUserDto) {
        User user = new User();
        user.setEmail(newUserDto.getEmail());
        user.setName(newUserDto.getName());
        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            userDtoList.add(toUserDto(user));
        }
        return userDtoList;
    }
}

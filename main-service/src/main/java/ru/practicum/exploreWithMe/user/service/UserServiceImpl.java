package ru.practicum.exploreWithMe.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.user.dto.NewUserDto;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.mapper.UserMapper;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        List<User> userList = repository.findAll();
        for (User user : userList) {
            if (user.getEmail().equals(newUserDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(newUserDto)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Page<User> page = repository.findByIdIn(ids, PageRequest.of(from, size));
        return UserMapper.toUserDtoList(page.getContent());
    }

    @Override
    public void deleteUser(long userId) {
        repository.findUserById(userId);
        repository.deleteById(userId);
    }
}

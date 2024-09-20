package ru.practicum.exploreWithMe.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.error.exceptions.ConflictException;
import ru.practicum.exploreWithMe.subscription.repository.SubscriptionRepository;
import ru.practicum.exploreWithMe.user.dto.NewUserDto;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.mapper.UserMapper;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public UserDto createUser(NewUserDto newUserDto) {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            if (user.getEmail().equals(newUserDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
        }
        UserDto savedUserDto = UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserDto)));
        savedUserDto.setSubscriberCount(0);
        return savedUserDto;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Page<User> page = userRepository.findByIdIn(ids, PageRequest.of(from, size));
        List<User> userList = page.getContent();
        List<Long> idsForFindSubscriberCount = new ArrayList<>();
        for (User user : userList) {
            idsForFindSubscriberCount.add(user.getId());
        }

        List<Map<String, Object>> results = subscriptionRepository.countSubscribersByUserIds(idsForFindSubscriberCount);
        Map<Long, Long> subscriberCounts = new HashMap<>();

        for (Map<String, Object> map : results) {
            Long userId = ((Number) map.get("userId")).longValue();
            Long count = ((Number) map.get("count")).longValue();
            subscriberCounts.put(userId, count);
        }

        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : userList) {
            UserDto dto = UserMapper.toUserDto(user);
            dto.setSubscriberCount(subscriberCounts.getOrDefault(user.getId(), 0L));
            userDtoList.add(dto);
        }
        return userDtoList;
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.findUserById(userId);
        userRepository.deleteById(userId);
    }
}

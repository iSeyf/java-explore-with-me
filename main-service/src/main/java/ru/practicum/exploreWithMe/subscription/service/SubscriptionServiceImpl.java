package ru.practicum.exploreWithMe.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.ViewStatsDto;
import ru.practicum.exploreWithMe.error.exceptions.BadRequestException;
import ru.practicum.exploreWithMe.error.exceptions.NotFoundException;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.mappers.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.stats.service.StatsService;
import ru.practicum.exploreWithMe.subscription.dto.SubscriberInfoDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriptionDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriptionInfoDto;
import ru.practicum.exploreWithMe.subscription.mapper.SubscriptionMapper;
import ru.practicum.exploreWithMe.subscription.model.Subscription;
import ru.practicum.exploreWithMe.subscription.repository.SubscriptionRepository;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final StatsService statsService;

    @Transactional
    @Override
    public SubscriptionDto subscribeToUser(long userId, long subscribedToId) {
        User user = userRepository.findUserById(userId);
        User subscribedToUser = userRepository.findUserById(subscribedToId);

        if (userId == subscribedToId) {
            throw new BadRequestException("Нельзя подписаться на самого себя");
        }

        if (subscriptionRepository.existsByUserIdAndSubscribedToId(userId, subscribedToId)) {
            throw new BadRequestException("Вы уже подписаны на этого пользователя");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscribedTo(subscribedToUser);
        subscription.setCreated(LocalDateTime.now());
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        userRepository.save(subscribedToUser);
        return SubscriptionMapper.toSubscriptionDto(savedSubscription);
    }

    @Transactional
    @Override
    public void unsubscribe(long userId, long subscribedToId) {
        userRepository.findUserById(userId);
        userRepository.findUserById(subscribedToId);

        if (!subscriptionRepository.existsByUserIdAndSubscribedToId(userId, subscribedToId)) {
            throw new NotFoundException("Подписка пользователя с ID " + userId + " на пользователя с ID " + subscribedToId + " не найдена.");
        }

        subscriptionRepository.deleteByUserIdAndSubscribedToId(userId, subscribedToId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubscriberInfoDto> getUserSubscribers(long userId) {
        userRepository.findUserById(userId);
        List<Subscription> subscriptions = subscriptionRepository.findBySubscribedToId(userId);
        return SubscriptionMapper.toSubscriberInfoDtoList(subscriptions);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubscriptionInfoDto> getUserSubscriptions(long userId) {
        userRepository.findUserById(userId);
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return SubscriptionMapper.toSubscriptionInfoDtoList(subscriptions);
    }

    @Transactional
    @Override
    public List<EventShortDto> getFeed(long userId, int from, int size) {
        userRepository.findUserById(userId);
        List<Long> subscribedUserIds = subscriptionRepository.findSubscribedUserIdsByUserId(userId);

        Page<Event> subscribedUsersEvents = eventRepository.getFeed(subscribedUserIds, PageRequest.of(from, size));
        List<Event> events = subscribedUsersEvents.getContent();
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if (!events.isEmpty()) {
            List<String> eventUris = new ArrayList<>();
            for (Event event : events) {
                eventUris.add("/events/" + event.getId());
            }

            List<ViewStatsDto> viewStats = statsService.getViewStats(events.get(0).getPublishedOn().minusSeconds(1), LocalDateTime.now(), eventUris);

            Map<String, Long> viewsMap = new HashMap<>();
            for (ViewStatsDto stat : viewStats) {
                viewsMap.put(stat.getUri(), stat.getHits());
            }

            for (Event event : events) {
                EventShortDto dto = EventMapper.toEventShortDto(event);
                String eventUri = "/events/" + event.getId();
                dto.setViews(viewsMap.getOrDefault(eventUri, 0L));
                eventShortDtoList.add(dto);
            }
            eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
        }
        return eventShortDtoList;
    }
}

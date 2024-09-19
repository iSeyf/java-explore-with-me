package ru.practicum.exploreWithMe.subscription.service;

import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriberInfoDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriptionDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriptionInfoDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto subscribeToUser(long userId, long subscribedToId);

    void unsubscribe(long userId, long subscribedToId);

    List<SubscriberInfoDto> getUserSubscribers(long userId);

    List<SubscriptionInfoDto> getUserSubscriptions(long userId);

    List<EventShortDto> getFeed(long userId, int from, int size);
}

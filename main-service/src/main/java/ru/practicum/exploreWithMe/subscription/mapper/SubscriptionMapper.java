package ru.practicum.exploreWithMe.subscription.mapper;

import ru.practicum.exploreWithMe.subscription.dto.SubscriberInfoDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriptionDto;
import ru.practicum.exploreWithMe.subscription.dto.SubscriptionInfoDto;
import ru.practicum.exploreWithMe.subscription.model.Subscription;
import ru.practicum.exploreWithMe.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionMapper {
    public static SubscriberInfoDto toSubscriberInfoDto(Subscription subscription) {
        return new SubscriberInfoDto(UserMapper.toUserShortDto(subscription.getUser()), subscription.getCreated());
    }

    public static SubscriptionInfoDto toSubscriptionInfoDto(Subscription subscription) {
        return new SubscriptionInfoDto(UserMapper.toUserShortDto(subscription.getSubscribedTo()), subscription.getCreated());
    }

    public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
        return new SubscriptionDto(UserMapper.toUserShortDto(subscription.getUser()),
                UserMapper.toUserShortDto(subscription.getSubscribedTo()),
                subscription.getCreated());
    }

    public static List<SubscriptionInfoDto> toSubscriptionInfoDtoList(List<Subscription> subscriptions) {
        List<SubscriptionInfoDto> subscriptionInfoDtoList = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            subscriptionInfoDtoList.add(toSubscriptionInfoDto(subscription));
        }
        return subscriptionInfoDtoList;
    }

    public static List<SubscriberInfoDto> toSubscriberInfoDtoList(List<Subscription> subscriptions) {
        List<SubscriberInfoDto> subscriberInfoDtoList = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            subscriberInfoDtoList.add(toSubscriberInfoDto(subscription));
        }
        return subscriberInfoDtoList;
    }
}

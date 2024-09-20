package ru.practicum.exploreWithMe.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.exploreWithMe.subscription.model.Subscription;

import java.util.List;
import java.util.Map;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserIdAndSubscribedToId(long subscriberId, long subscribedToId);

    List<Subscription> findByUserId(long userId);

    List<Subscription> findBySubscribedToId(long subscribedToId);

    void deleteByUserIdAndSubscribedToId(long userId, long subscribedToId);

    @Query("SELECT s.subscribedTo.id FROM Subscription s WHERE s.user.id = :userId")
    List<Long> findSubscribedUserIdsByUserId(@Param("userId") long userId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.subscribedTo.id = :userId")
    long countSubscribers(@Param("userId") long userId);

    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.user.id = :userId AND s.subscribedTo.id = :subscribedToId")
    boolean isSubscribed(@Param("userId") long userId, @Param("subscribedToId") long subscribedToId);

    @Query("SELECT new map(s.subscribedTo.id as userId, COUNT(s) as count) FROM Subscription s WHERE s.subscribedTo.id IN :ids GROUP BY s.subscribedTo.id")
    List<Map<String, Object>> countSubscribersByUserIds(@Param("ids") List<Long> ids);
}

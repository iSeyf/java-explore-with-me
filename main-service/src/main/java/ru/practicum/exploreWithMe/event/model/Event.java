package ru.practicum.exploreWithMe.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "confirmed_requests")
    private long confirmedRequests;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator", nullable = false)
    private User initiator;
    @Column(nullable = false)
    private float lat;
    @Column(nullable = false)
    private float lon;
    @Column(nullable = false)
    private boolean paid;
    @Column(name = "participant_limit")
    private long participantLimit;
    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;
    @Column(nullable = false)
    private String title;
}

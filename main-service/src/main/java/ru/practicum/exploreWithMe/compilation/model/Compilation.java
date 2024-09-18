package ru.practicum.exploreWithMe.compilation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.event.model.Event;

import java.util.List;

@Entity
@Table(name = "compilations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @Column(name = "compilation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "pinned")
    private boolean pinned;
    @Column(name = "title")
    private String title;
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;
}

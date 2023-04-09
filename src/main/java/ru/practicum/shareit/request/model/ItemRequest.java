package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(name = "item_requests", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor", referencedColumnName = "id", nullable = false)
    private User requestor;
    @Column(nullable = false)
    private LocalDateTime created;
}

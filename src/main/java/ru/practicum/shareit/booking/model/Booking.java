package ru.practicum.shareit.booking.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_ts", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_ts", nullable = false)
    private LocalDateTime end;

    @OneToOne
    @JoinColumn(name = "item", referencedColumnName = "id", nullable = false)
    private Item item;

    @OneToOne
    @JoinColumn(name = "booker", referencedColumnName = "id")
    private User booker;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingState status;
}

package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    public Collection<Comment> getCommentsByItem(Item item);
}

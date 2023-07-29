package ru.practicum.shareit.fixtures;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentFixture {
    private static Long idSequence = 1L;

    public static Comment getComment(Item item, User author) {
        return getComment(idSequence++, item, author);
    }

    public static Comment getComment(Long id, Item item, User author) {
        return new Comment(
                id,
                "dummy comment",
                item,
                author,
                LocalDateTime.now()
        );
    }
}

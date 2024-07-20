package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FeedEntry;

import java.util.List;

public interface FeedStorage {
    FeedEntry create(FeedEntry feedEntry);

    List<FeedEntry> getUserFeed(int userId);

}

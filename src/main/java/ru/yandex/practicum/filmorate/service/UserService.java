package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEntry;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FeedService feedService;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        if (userStorage.findUserById(user.getId()).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return userStorage.update(user);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public void addFriend(int id, int friendId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        if (id < 0 || friendId < 0) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        friendStorage.addFriend(id, friendId);

        FeedEntry feedEntry = FeedEntry.builder()
                .userId(id)
                .eventType(FeedEventType.FRIEND)
                .operation(FeedOperationType.ADD)
                .entityId(friendId)
                .build();

        feedService.create(feedEntry);
    }

    public List<User> findAllFriends(int id) {
        if (userStorage.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return friendStorage.findAllFriends(id);
    }

    public List<User> findCommonFriends(int id, int otherId) {
        return friendStorage.findCommonFriends(id, otherId);
    }

    public void removeFriend(int id, int friendId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        friendStorage.removeFriend(id, friendId);

        FeedEntry feedEntry = FeedEntry.builder()
                .userId(id)
                .eventType(FeedEventType.FRIEND)
                .operation(FeedOperationType.REMOVE)
                .entityId(friendId)
                .build();

        feedService.create(feedEntry);
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void deleteUserById(int id) {
        userStorage.findUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        userStorage.deleteUserById(id);
    }
}
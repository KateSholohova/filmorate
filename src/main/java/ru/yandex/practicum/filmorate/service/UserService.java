package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(long id, long friendId) {

        if (inMemoryUserStorage.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        if (inMemoryUserStorage.findById(friendId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + friendId);
        }
        User user = inMemoryUserStorage.findById(id);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        user.getFriends().add(friendId);
        User friend = inMemoryUserStorage.findById(friendId);
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }
        friend.getFriends().add(id);
        inMemoryUserStorage.update(user);
        inMemoryUserStorage.update(friend);
        return friend;
    }

    public Set<Long> deleteUser(long id, long friendId) {
        if (inMemoryUserStorage.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        if (inMemoryUserStorage.findById(friendId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + friendId);
        }
        User user = inMemoryUserStorage.findById(id);
        if (user.getFriends() == null) {
            return new HashSet<>();
        }
        if (!user.getFriends().contains(friendId)) {
            throw new NotFoundException("Нет пользователя с id у вас в друзьях: " + id);
        }
        user.getFriends().remove(friendId);
        User friend = inMemoryUserStorage.findById(friendId);
        friend.getFriends().remove(id);
        inMemoryUserStorage.update(user);
        inMemoryUserStorage.update(friend);
        return user.getFriends();
    }

    public List<User> commonFriends(long id, long otherId) {
        if (inMemoryUserStorage.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        if (inMemoryUserStorage.findById(otherId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + otherId);
        }
        User user = inMemoryUserStorage.findById(id);
        User other = inMemoryUserStorage.findById(otherId);
        List<User> commonFriends = new ArrayList<>();
        for (Long firstId : user.getFriends()) {
            if (other.getFriends().contains(firstId)) {
                commonFriends.add(inMemoryUserStorage.findById(firstId));
            }
        }
        return commonFriends;
    }

    public List<User> allFriends(long id) {
        if (inMemoryUserStorage.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        User user = inMemoryUserStorage.findById(id);
        List<User> allFriends = new ArrayList<>();
        if (user.getFriends() == null) {
            return allFriends;
        }
        for (Long firstId : user.getFriends()) {
            if (inMemoryUserStorage.findById(firstId) != null) {
                allFriends.add(inMemoryUserStorage.findById(firstId));
            }
        }
        return allFriends;
    }

    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    public void delete(long id) {
        inMemoryUserStorage.delete(id);
    }

    public User update(User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }


}

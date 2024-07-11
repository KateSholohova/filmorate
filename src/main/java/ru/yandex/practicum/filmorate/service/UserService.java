package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userRepository;
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public User addFriend(long id, long friendId) {

        if (userRepository.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        if (userRepository.findById(friendId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + friendId);
        }
        jdbc.update("INSERT INTO friendship_request(from_user_id, to_user_id, status) " +
                "VALUES(?, ?, ?)", id, friendId, 1);
        jdbc.update("INSERT INTO friendship_request(from_user_id, to_user_id, status) " +
                "VALUES(?, ?, ?)", friendId, id, 0);


        return userRepository.findById(id);
    }

    public User deleteUser(long id, long friendId) {
        if (userRepository.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        if (userRepository.findById(friendId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + friendId);
        }
        jdbc.update("DELETE FROM friendship_request WHERE from_user_id = ? AND to_user_id = ? AND " +
                "status = ?", id, friendId, 1);
        jdbc.update("DELETE FROM friendship_request WHERE from_user_id = ? AND to_user_id = ? AND " +
                "status = ?", friendId, id, 0);


        return userRepository.findById(id);
    }


    public List<User> commonFriends(long id, long otherId) {
        if (userRepository.findById(id) == null) {
            throw new NotFoundException("Нет пользователя с id: " + id);
        }
        if (userRepository.findById(otherId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + otherId);
        }

        String sql = "SELECT to_user_id FROM friendship_request WHERE from_user_id = ?" +
                " INTERSECT " +
                "SELECT to_user_id FROM friendship_request WHERE from_user_id = ?";

        List<Long> commonFriendIds = jdbc.queryForList(sql, Long.class, id, otherId);

        // Получить список пользователей по их ID
        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : commonFriendIds) {
            User user = userRepository.findById(friendId);
            if (user != null) {
                commonFriends.add(user);
            }
        }

        return commonFriends;
    }

    public List<User> allFriends(long userId) {
        if (userRepository.findById(userId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + userId);
        }
        String sql = "SELECT U.* FROM friendship_request AS F JOIN USERS AS U ON F.to_user_id = U.id WHERE F.from_user_id = ? AND F.status = true";
        List<User> usersList = jdbc.query(sql, mapper, userId);

        return usersList;
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public void delete(long id) {
        userRepository.delete(id);
    }

    public User update(User newUser) {
        return userRepository.update(newUser);
    }

    public User create(User user) {
        return userRepository.create(user);
    }


}

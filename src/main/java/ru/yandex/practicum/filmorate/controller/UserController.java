package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FeedEntry;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;
    private final FeedService feedService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST / user / {}", user.getLogin());
        userService.create(user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT / user / {}", user.getLogin());
        userService.update(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        log.info("Удален пользователь id {}", id);
        userService.deleteUserById(id);
    }

    @GetMapping
    public List<User> findAll() {
        log.info("GET / users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") int id) {
        log.info("GET / users / {}", id);
        return userService.findUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("PUT / {} / friends / {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("DELETE / {} / friends / {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable("id") int id) {
        log.info("GET / {} / friends", id);
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        log.info("GET / {} / friends / common / {}", id, otherId);
        return userService.findCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEntry> getFeed(@PathVariable("id") int id) {
        log.info("GET / {} / feed", id);
        return feedService.getUserFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> findRecommendedFilms(@PathVariable("id") int id) {
        log.info("GET / {} / recommendations", id);
        return filmService.findRecommendedFilms(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FeedEntry;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final FeedService feedService;

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film create(Film film) {
        Optional<Mpa> mpa = mpaStorage.findMpaById(film.getMpa().getId());
        if (mpa.isEmpty()) {
            throw new ValidationException("Mpa not found");
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> genreStorage.findGenreById(genre.getId()).orElseThrow(() -> new ValidationException("Жанр " + genre.getId() + " не найден.")));
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (filmStorage.findFilmById(film.getId()).isEmpty()) {
            throw new FilmNotFoundException("Фильм не найден.");
        }
        return filmStorage.update(film);
    }

    public Film findFilmById(int id) {
        Film film = filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));

        List<Genre> genres = genreStorage.findAllGenresByFilmID(id);
        LinkedHashSet<Genre> genresSet = new LinkedHashSet<>(genres);

        genresSet.addAll(genres);

        film.setGenres(genresSet);

        return film;
    }

    public void addLike(int id, int userId) {
        userStorage.findUserById(userId).orElseThrow(() -> new FilmNotFoundException("Пользователь не найден."));
        filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));

        likeStorage.addLike(id, userId);

        FeedEntry feedEntry = FeedEntry.builder()
                .userId(userId)
                .eventType(FeedEventType.LIKE)
                .operation(FeedOperationType.ADD)
                .entityId(id)
                .build();

        feedService.create(feedEntry);
    }

    public void removeLike(int id, int userId) {
        userStorage.findUserById(userId).orElseThrow(() -> new FilmNotFoundException("Пользователь не найден."));
        filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));

        likeStorage.removeLike(id, userId);
        FeedEntry feedEntry = FeedEntry.builder()
                .userId(userId)
                .eventType(FeedEventType.LIKE)
                .operation(FeedOperationType.REMOVE)
                .entityId(id)
                .build();

        feedService.create(feedEntry);

    }

    public List<Film> findPopular(int count) {
        return filmStorage.findPopular(count);
    }

    public List<Film> findFilmsByDirectorID(int id, String sortedBy) {
        return filmStorage.findFilmsByDirectorID(id, sortedBy);
    }

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(int id) {
        return mpaStorage.findMpaById(id).orElseThrow(() -> new MpaNotFoundException("Рейтинг MPA не найден."));
    }

    public List<Genre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public Genre findGenreById(int id) {
        return genreStorage.findGenreById(id).orElseThrow(() -> new GenreNotFoundException("Жанр не найден."));
    }

    public void deleteFilmById(int id) {
        filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));
        filmStorage.deleteFilmById(id);
    }

    public List<Film> findRecommendedFilms(int userId) {
        userStorage.findUserById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
        return filmStorage.findRecommendedFilms(userId);
    }

    public List<Film> searchFilm(String query, List<String> by) {
        if (by.size() == 1 && by.contains("title")) {
            return filmStorage.searchFilmsByName(query);
        }
        if (by.size() == 1 && by.contains("director")) {
            return filmStorage.searchFilmsByDir(query);
        }
        return filmStorage.searchFilmsByDirAndName(query);
    }

}
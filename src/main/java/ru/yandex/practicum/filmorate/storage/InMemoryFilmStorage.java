package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long count = 0L;


    @Override
    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Некорректная дата выхода: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(identify());
        films.put(film.getId(), film);
        log.info("Фильм создан: {}", film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
                log.error("Некорректная дата выхода: {}", newFilm.getReleaseDate());
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }

            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }


            log.info("Фильм обновлен: {}", oldFilm);
            return oldFilm;
        }
        log.error("Нет фильма с данным id: {}", newFilm.getId());
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public void delete(long id) {
        if (films.containsKey(id)) {
            films.remove(id);
        }
    }

    public long identify() {
        return ++count;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film findById(long id) {
        return films.get(id);
    }
}

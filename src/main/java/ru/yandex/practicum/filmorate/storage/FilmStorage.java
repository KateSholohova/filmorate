package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public interface FilmStorage {
    public Film create(Film film);

    public Film update(Film film);

    public void delete(long id);

}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    List<Director> findAllDirectors();

    Optional<Director> findDirectorById(int id);

    void removeDirectorById(int id);
}

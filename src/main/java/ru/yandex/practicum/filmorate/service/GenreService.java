package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre findById(long id) {
        return genreDbStorage.findById(id);
    }
}

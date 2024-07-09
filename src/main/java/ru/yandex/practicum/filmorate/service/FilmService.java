package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Film addLike(long id, long userId) {
        if (inMemoryFilmStorage.findById(id) == null) {
            throw new NotFoundException("Нет фильма с id: " + id);
        }
        if (inMemoryUserStorage.findById(userId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + userId);
        }
        Film film = inMemoryFilmStorage.findById(id);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
        inMemoryFilmStorage.update(film);
        return film;
    }

    public Set<Long> deleteLikes(long id, long userId) {
        if (inMemoryFilmStorage.findById(id) == null) {
            throw new NotFoundException("Нет фильма с id: " + id);
        }
        if (inMemoryUserStorage.findById(userId) == null) {
            throw new NotFoundException("Нет пользователя с id: " + userId);
        }
        Film film = inMemoryFilmStorage.findById(id);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не ставил лайк фильму " + id);
        }
        film.getLikes().remove(userId);
        inMemoryFilmStorage.update(film);
        return film.getLikes();
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    public void delete(long id) {
        inMemoryFilmStorage.delete(id);
    }

    public Film update(Film film) {
        return inMemoryFilmStorage.update(film);
    }

    public Film create(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public List<Film> getPopular(int count) {
        ArrayList<Film> films = new ArrayList<>(inMemoryFilmStorage.getFilms().values());
        Comparator<Film> filmComparator = new Comparator<Film>() {
            public int compare(Film film1, Film film2) {
                System.out.println(film1.getId());
                System.out.println(film2.getId());
                if (film1.getLikes() == null) {
                    film1.setLikes(new HashSet<>());
                }
                if (film2.getLikes() == null) {
                    film2.setLikes(new HashSet<>());
                }
                return film2.getLikes().size() - film1.getLikes().size();
            }
        };
        Collections.sort(films, filmComparator);
        List<Film> popularFilms = new ArrayList<>();
        if (count != 0) {
            if (count > films.size()) {
                popularFilms = films;
            } else {
                popularFilms = (films.subList(0, count));
            }
        } else if (count == 0 && films.size() < 10) {
            popularFilms = (films);
        } else if (count == 0 && films.size() >= 10) {
            popularFilms = (films.subList(0, 10));
        }
        return popularFilms;
    }


}

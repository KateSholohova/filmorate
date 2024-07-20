package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(int id);

    List<Review> findAll(int limit);

    List<Review> findByFilmId(int filmId, int limit);

    Optional<Review> findById(int id);

    boolean isAlreadyExists(Review review);

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);
}

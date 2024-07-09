package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private static FilmStorage inMemoryFilmStorage;
    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        inMemoryFilmStorage = new InMemoryFilmStorage();
    }

    @Test
    void testCreateFilm() {
        Film film = new Film();
        film.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        film.setName("names");
        film.setDescription("ааааааааааааааааааааааааааааааааgfffffffffffffffffffffffffffffffffffffffааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа");
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        film.setDescription("a");
        film.setReleaseDate(LocalDate.parse("1890-12-28"));
        assertThrows(ValidationException.class, () -> {
            inMemoryFilmStorage.create(film);
        });
        film.setReleaseDate(LocalDate.parse("1900-12-28"));
        film.setDuration(-1);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

}
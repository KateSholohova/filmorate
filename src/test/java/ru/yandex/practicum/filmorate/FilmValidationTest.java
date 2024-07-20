package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {
    private Film film;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validateFilmName() {
        film = Film.builder()
                .id(1)
                .name("")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Введите название фильма.", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmDescription() {
        film = Film.builder()
                .id(1)
                .name("Film")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                        "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о " +
                        "Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Слишком длинное описание.", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmReleaseDate() {
        film = Film.builder()
                .id(1)
                .name("Film")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Введите дату релиза не ранее 28 декабря 1895 года.",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmDuration() {
        film = Film.builder()
                .id(1)
                .name("Film")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(-1)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть больше 0.",
                violations.iterator().next().getMessage());
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserControllerTest {
    private static UserStorage userStorage;
    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        user.setEmail("email@");
        user.setLogin(" ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        user.setLogin("a");
        user.setBirthday(LocalDate.parse("2027-12-28"));
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        user.setBirthday(LocalDate.parse("2000-12-28"));
        userStorage.create(user);

    }
}
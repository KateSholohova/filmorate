package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    String name;
    Long id;
    Set<Long> friends;
    @NotBlank
    @Email
    String email;
    @NotBlank
    String login;
    @Past
    LocalDate birthday;
}

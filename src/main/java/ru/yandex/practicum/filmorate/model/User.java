package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private String name;
    private Long id;
    private Set<Long> friends;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    @Past
    private LocalDate birthday;
}

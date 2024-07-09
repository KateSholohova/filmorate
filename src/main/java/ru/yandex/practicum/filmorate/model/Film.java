package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    Long id;
    LocalDate releaseDate;
    private Mpa mpa;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @Positive
    Integer duration;
}

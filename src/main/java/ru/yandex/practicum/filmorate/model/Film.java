package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    Long id;
    LocalDate releaseDate;
    Set<Long> likes;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @Positive
    Integer duration;
}

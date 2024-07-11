package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

@Data
public class Film {
    Long id;
    LocalDate releaseDate;
    Mpa mpa;
    @Nullable
    List<Genre> genres;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @Positive
    Integer duration;
}

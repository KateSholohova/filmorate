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
    private Long id;
    private LocalDate releaseDate;
    private Mpa mpa;
    @Nullable
    private List<Genre> genres;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @Positive
    private Integer duration;
}

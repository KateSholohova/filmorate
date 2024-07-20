package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    @JsonProperty("reviewId")
    private int id;

    @NotBlank(message = "Cодержание отзыва не может быть пустым.")
    @Size(max = 1000, message = "Слишком длинное содержание.")
    private String content;

    @NotNull
    private Boolean isPositive;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    private Integer useful;
}

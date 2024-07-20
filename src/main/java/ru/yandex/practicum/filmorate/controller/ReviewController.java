package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody @Valid Review review) {
        log.info("POST / review / {}", review.getContent());
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        log.info("PUT / review / {}", review.getContent());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("DELETE / review / {}", id);
        reviewService.delete(id);
    }

    @GetMapping()
    public List<Review> findMany(@RequestParam Optional<Integer> filmId,
                                 @RequestParam Optional<Integer> count) {
        int limit = count.orElse(10);

        if (filmId.isEmpty()) {
            log.info("GET / reviews");
            return reviewService.findAll(limit);
        }

        log.info("GET / reviews with filmId {}", filmId.get());
        return reviewService.findByFilmId(filmId.get(), limit);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable int id) {
        log.info("GET / {}", id);
        return reviewService.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT / review / {} / like / {}", id, userId);
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT / review / {} / dislike / {}", id, userId);
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE / review / {} / like / {}", id, userId);
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE / review / {} / dislike / {}", id, userId);
        return reviewService.deleteDislike(id, userId);
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;


@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("directors")
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("POST / director / {}", director.getName());
        directorService.create(director);
        return director;
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("PUT / director / {}", director.getName());
        directorService.update(director);
        return director;
    }

    @GetMapping
    public List<Director> findAllDirectors() {
        log.info("GET / directors");
        return directorService.findAllDirectors();
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable("id") int id) {
        log.info("GET / {}", id);
        return directorService.findDirectorById(id);
    }

    @DeleteMapping("/{id}")
    public void removeDirectorById(@PathVariable("id") int id) {
        log.info("DELETE / {} ", id);
        directorService.removeDirectorById(id);
    }

}

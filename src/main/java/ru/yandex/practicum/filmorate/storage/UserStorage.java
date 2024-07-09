package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;


public interface UserStorage {


    public User create(User user);

    public User update(User user);

    public void delete(long id);
}

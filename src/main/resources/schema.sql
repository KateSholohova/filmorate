CREATE TABLE IF NOT EXISTS users
(
    id
    INTEGER AUTO_INCREMENT,
    email
    varchar
    NOT
    NULL,
    login
    varchar
    NOT
    NULL,
    name
    varchar,
    birthday
    date
    NOT
    NULL,
    PRIMARY
    KEY
(
    id
)

    );

CREATE TABLE IF NOT EXISTS mpa
(
    id
    INTEGER AUTO_INCREMENT,
    name
    varchar
    NOT
    NULL,
    PRIMARY
    KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS films
(
    id
    INTEGER AUTO_INCREMENT,
    name
    varchar
    NOT
    NULL,
    description
    varchar
(
    200
),
    releaseDate date,
    duration INTEGER,
    mpa_id INTEGER REFERENCES mpa
(
    id
),
    PRIMARY KEY
(
    id
),
    CONSTRAINT duration_positive CHECK
(
    duration >
    0
)

    );

CREATE TABLE IF NOT EXISTS films_likes
(

    film_id
    INTEGER
    REFERENCES
    films
(
    id
),
    user_id INTEGER REFERENCES users
(
    id
)
    );

CREATE TABLE IF NOT EXISTS friendship_request
(
    from_user_id
    INTEGER
    REFERENCES
    users
(
    id
),
    to_user_id INTEGER REFERENCES users
(
    id
),
    status BIT
    );

CREATE TABLE IF NOT EXISTS genre
(
    id
    INTEGER AUTO_INCREMENT,
    name
    varchar
    NOT
    NULL,
    PRIMARY
    KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS films_genre
(
    film_id
    INTEGER
    REFERENCES
    films
(
    id
),
    genre_id INTEGER REFERENCES genre
(
    id
)
    );


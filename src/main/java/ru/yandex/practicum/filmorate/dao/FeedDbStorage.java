package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEntry;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public static final String GET_USER_FEED = "SELECT EVENT_TIMESTAMP, USER_ID, EVENT_TYPE , OPERATION , EVENT_ID , ENTITY_ID " +
            "FROM FEED " +
            "WHERE USER_ID = ?;";
    public static final String ADD_FEED_ENTRY = "INSERT INTO FEED(EVENT_TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION , ENTITY_ID) " +
            "values(?, ?, ?, ?, ?);";


    @Override
    public FeedEntry create(FeedEntry feedEntry) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(ADD_FEED_ENTRY, new String[]{"event_id"});
                    ps.setTimestamp(1, Timestamp.from(Instant.ofEpochMilli(feedEntry.getTimestamp())));
                    ps.setInt(2, feedEntry.getUserId());
                    ps.setString(3, feedEntry.getEventType().toString());
                    ps.setString(4, feedEntry.getOperation().toString());
                    ps.setInt(5, feedEntry.getEntityId());
                    return ps;
                }, keyHolder);
        feedEntry.setEventId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return feedEntry;
    }

    @Override
    public List<FeedEntry> getUserFeed(int userId) {
        return jdbcTemplate.query(GET_USER_FEED, (rs, rowNum) -> makeFeedEntry(rs), userId);
    }

    private FeedEntry makeFeedEntry(ResultSet rs) throws SQLException {
        return FeedEntry.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .timestamp(rs.getTimestamp("EVENT_TIMESTAMP").toInstant().toEpochMilli())
                .userId(rs.getInt("USER_ID"))
                .eventType(FeedEventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(FeedOperationType.valueOf(rs.getString("OPERATION")))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }

}
